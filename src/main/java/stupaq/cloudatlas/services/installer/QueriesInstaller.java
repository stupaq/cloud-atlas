package stupaq.cloudatlas.services.installer;

import com.google.common.util.concurrent.AbstractIdleService;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.HierarchicalINIConfiguration;
import org.apache.commons.configuration.event.ConfigurationEvent;
import org.apache.commons.configuration.event.ConfigurationListener;
import org.apache.commons.configuration.reloading.FileChangedReloadingStrategy;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.rmi.RemoteException;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;

import stupaq.cloudatlas.attribute.values.CAQuery;
import stupaq.cloudatlas.configuration.CAConfiguration;
import stupaq.cloudatlas.naming.AttributeName;
import stupaq.cloudatlas.naming.GlobalName;
import stupaq.cloudatlas.services.rmiserver.protocol.LocalClientProtocol;

import static org.apache.commons.configuration.AbstractConfiguration.EVENT_ADD_PROPERTY;
import static org.apache.commons.configuration.AbstractConfiguration.EVENT_SET_PROPERTY;
import static org.apache.commons.configuration.AbstractFileConfiguration.EVENT_RELOAD;

public class QueriesInstaller extends AbstractIdleService implements QueriesInstallerConfigKeys {
  private static final Log LOG = LogFactory.getLog(QueriesInstaller.class);
  private final String queriesPath;
  private final LocalClientProtocol client;
  private final ScheduledExecutorService executor;
  private ConfigurationListener listener;
  private CAConfiguration caConfig;

  public QueriesInstaller(CAConfiguration configuration, LocalClientProtocol client,
      ScheduledExecutorService executor) {
    this.queriesPath = configuration.getString(QUERIES_FILE);
    this.client = client;
    this.executor = executor;
  }

  @Override
  protected ScheduledExecutorService executor() {
    return executor;
  }

  @Override
  protected void startUp() throws ConfigurationException {
    if (queriesPath == null) {
      LOG.warn("Queries file not set, nothing to do");
      return;
    }
    // Try to retrieve configuration
    HierarchicalINIConfiguration queriesConfig;
    try {
      queriesConfig = new HierarchicalINIConfiguration(queriesPath);
      caConfig = new CAConfiguration(queriesConfig);
    } catch (ConfigurationException e) {
      LOG.error("Failed to load queries file: " + queriesPath);
      throw e;
    }
    queriesConfig.setReloadingStrategy(new FileChangedReloadingStrategy());
    // Process all sections for the first time
    for (String section : queriesConfig.getSections()) {
      processSection(section);
    }
    // Set up a listener to process section's changes in the future
    listener = new ConfigurationListener() {
      @Override
      public void configurationChanged(ConfigurationEvent event) {
        if (!event.isBeforeUpdate()) {
          switch (event.getType()) {
            case EVENT_ADD_PROPERTY:
            case EVENT_SET_PROPERTY:
            case EVENT_RELOAD:
              processSection(sectionOf(event.getPropertyName()));
              break;
          }
        }
      }
    };
    queriesConfig.addConfigurationListener(listener);
  }

  @Override
  protected void shutDown() throws Exception {
    caConfig.removeConfigurationListener(listener);
    listener = null;
    caConfig = null;
  }

  private void processSection(String section) {
    LOG.info("Processing query entry: " + section);
    AttributeName name = AttributeName.special(section);
    List<GlobalName> zones = caConfig.getGlobalNames(section + ZONES_KEY);
    try {
      if (caConfig.containsKey(section + REMOVE_KEY)) {
        client.removeQuery(name, zones);
        caConfig.subset(section).clear();
      } else if (caConfig.containsKey(section + QUERY_KEY)) {
        client.installQuery(name, new CAQuery(caConfig.getString(section + QUERY_KEY)), zones);
      } else {
        LOG.error("Malformed query entry: " + section);
      }
    } catch (RemoteException e) {
      LOG.error("Error processing query entry: " + section, e);
    }
  }

  private String sectionOf(String key) {
    return key.split(".")[0];
  }
}
