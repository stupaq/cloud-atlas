package stupaq.cloudatlas.services.installer;

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
import java.rmi.RemoteException;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import stupaq.cloudatlas.attribute.Attribute;
import stupaq.cloudatlas.attribute.values.CAQuery;
import stupaq.cloudatlas.configuration.BootstrapConfiguration;
import stupaq.cloudatlas.configuration.CAConfiguration;
import stupaq.cloudatlas.naming.AttributeName;
import stupaq.cloudatlas.naming.GlobalName;
import stupaq.cloudatlas.services.rmiserver.protocol.LocalClientProtocol;

import static com.google.common.base.Optional.of;

public class QueriesInstaller extends AbstractScheduledService
    implements QueriesInstallerConfigKeys {
  private static final Log LOG = LogFactory.getLog(QueriesInstaller.class);
  private final BootstrapConfiguration config;
  private final File queriesFile;
  private final LocalClientProtocol client;
  private final ScheduledExecutorService executor;
  private HierarchicalINIConfiguration queriesConfig;

  public QueriesInstaller(BootstrapConfiguration config, LocalClientProtocol client) {
    this.config = config;
    String source = config.getString(QUERIES_FILE);
    this.queriesFile = source == null ? null : new File(source);
    this.client = client;
    this.executor = config.threadManager().singleThreaded(QueriesInstaller.class);
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
  protected void startUp() throws ConfigurationException, RemoteException {
    if (queriesFile == null) {
      LOG.warn("Queries file not set, nothing to do");
      return;
    }
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
  protected void shutDown() throws Exception {
    config.threadManager().free(executor);
    if (queriesFile != null) {
      for (ConfigurationListener listener : queriesConfig.getConfigurationListeners()) {
        queriesConfig.removeConfigurationListener(listener);
      }
      queriesConfig = null;
    }
  }

  @Override
  protected Scheduler scheduler() {
    return new CustomScheduler() {
      @Override
      protected Schedule getNextSchedule() throws Exception {
        return new Schedule(config.getLong(POLL_INTERVAL, POLL_INTERVAL_DEFAULT),
            TimeUnit.MILLISECONDS);
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
    List<GlobalName> zones = queries.getGlobalNames(section + QUERY_ZONES);
    try {
      if (!queries.getBoolean(section + QUERY_ENABLED, QUERY_ENABLED_DEFAULT)) {
        queries.subset(section).clear();
      } else if (queries.containsKey(section + QUERY_CODE)) {
        CAQuery query = new CAQuery(queries.getString(section + QUERY_CODE));
        client.installQuery(new Attribute<>(name, query), of(zones));
      } else {
        LOG.error("Malformed query entry: " + section);
      }
    } catch (RemoteException e) {
      LOG.error("Error processing query entry: " + section, e);
    }
  }
}
