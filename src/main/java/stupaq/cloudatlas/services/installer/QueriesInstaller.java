package stupaq.cloudatlas.services.installer;

import com.google.common.base.Joiner;
import com.google.common.base.Optional;
import com.google.common.util.concurrent.AbstractScheduledService;

import org.apache.commons.configuration.AbstractFileConfiguration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.HierarchicalINIConfiguration;
import org.apache.commons.configuration.event.ConfigurationEvent;
import org.apache.commons.configuration.event.ConfigurationListener;
import org.apache.commons.configuration.reloading.FileChangedReloadingStrategy;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.File;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;

import stupaq.cloudatlas.attribute.Attribute;
import stupaq.cloudatlas.attribute.values.CAQuery;
import stupaq.cloudatlas.configuration.BootstrapConfiguration;
import stupaq.cloudatlas.configuration.CAConfiguration;
import stupaq.cloudatlas.configuration.StartIfPresent;
import stupaq.cloudatlas.naming.AttributeName;
import stupaq.cloudatlas.naming.GlobalName;
import stupaq.cloudatlas.services.rmiserver.RMIServerConfigKeys;
import stupaq.cloudatlas.services.rmiserver.protocol.LocalClientProtocol;
import stupaq.commons.util.concurrent.FastStartScheduler;

import static com.google.common.base.Optional.of;
import static stupaq.cloudatlas.services.rmiserver.RMIServer.createClient;

@StartIfPresent(section = "queries_installer")
public class QueriesInstaller extends AbstractScheduledService
    implements QueriesInstallerConfigKeys, RMIServerConfigKeys {
  private static final Log LOG = LogFactory.getLog(QueriesInstaller.class);
  private final BootstrapConfiguration config;
  private final File queriesFile;
  private final ScheduledExecutorService executor;
  private LocalClientProtocol client;
  private HierarchicalINIConfiguration queriesConfig;

  public QueriesInstaller(BootstrapConfiguration config) {
    config.mustContain(QUERIES_FILE);
    this.config = config;
    this.queriesFile = new File(config.getString(QUERIES_FILE));
    this.executor = config.threadModel().singleThreaded(QueriesInstaller.class);
  }

  @Override
  protected ScheduledExecutorService executor() {
    return executor;
  }

  @Override
  protected void runOneIteration() throws Exception {
    queriesConfig.reload();
  }

  @Override
  protected void startUp() throws ConfigurationException, RemoteException, NotBoundException {
    client = createClient(LocalClientProtocol.class, config);
    // Try to retrieve configuration
    try {
      queriesConfig = new HierarchicalINIConfiguration(queriesFile);
      queriesConfig.setReloadingStrategy(new FileChangedReloadingStrategy());
    } catch (ConfigurationException e) {
      LOG.error("Failed to load queries file: " + queriesFile);
      throw e;
    }
    // Remove all queries if we were to handover the agent
    if (queriesConfig.getBoolean(REPLACE_ALL, REPLACE_ALL_DEFAULT)) {
      client.removeQuery(Optional.<AttributeName>absent(), Optional.<List<GlobalName>>absent());
    }
    // Install queries from file
    processConfig();
    // Set up file monitor
    queriesConfig.addConfigurationListener(new ConfigurationListener() {
      @Override
      public void configurationChanged(ConfigurationEvent event) {
        if (!event.isBeforeUpdate()) {
          switch (event.getType()) {
            case AbstractFileConfiguration.EVENT_CONFIG_CHANGED:
            case AbstractFileConfiguration.EVENT_RELOAD:
              processConfig();
              break;
          }
        }
      }
    });
  }

  @Override
  protected void shutDown() {
    config.threadModel().free(executor);
    for (ConfigurationListener listener : queriesConfig.getConfigurationListeners()) {
      queriesConfig.removeConfigurationListener(listener);
    }
    queriesConfig = null;
    client = null;
  }

  @Override
  protected Scheduler scheduler() {
    return new FastStartScheduler() {
      @Override
      protected long getNextDelayMs() throws Exception {
        return config.getLong(POLL_INTERVAL, POLL_INTERVAL_DEFAULT);
      }
    };
  }

  private void processConfig() {
    for (String section : queriesConfig.getSections()) {
      if (section != null) {
        processSection(section);
      }
    }
  }

  private void processSection(String section) {
    LOG.info("Processing query entry: " + section);
    AttributeName name = AttributeName.special(section);
    CAConfiguration queries = new CAConfiguration(queriesConfig);
    Optional<List<GlobalName>> zones = queries.getGlobalNames(section + QUERY_ZONES);
    try {
      if (!queries.getBoolean(section + QUERY_ENABLED, QUERY_ENABLED_DEFAULT)) {
        client.removeQuery(of(name), zones);
      } else if (queries.containsKey(section + QUERY_CODE)) {
        String queryStr = Joiner.on(HierarchicalINIConfiguration.getDefaultListDelimiter())
            .join(queries.getStringArray(section + QUERY_CODE));
        CAQuery query = new CAQuery(queryStr);
        client.installQuery(new Attribute<>(name, query), zones);
      } else {
        LOG.error("Malformed query entry: " + section);
      }
    } catch (RemoteException e) {
      LOG.error("Error processing query entry: " + section, e);
    }
  }
}
