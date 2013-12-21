package stupaq.cloudatlas.services.installer;

import com.google.common.base.Optional;
import com.google.common.util.concurrent.AbstractIdleService;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.HierarchicalINIConfiguration;
import org.apache.commons.configuration.reloading.FileChangedReloadingStrategy;
import org.apache.commons.io.monitor.FileAlterationListenerAdaptor;
import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.apache.commons.io.monitor.FileAlterationObserver;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.File;
import java.rmi.RemoteException;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;

import stupaq.cloudatlas.attribute.Attribute;
import stupaq.cloudatlas.attribute.values.CAQuery;
import stupaq.cloudatlas.configuration.CAConfiguration;
import stupaq.cloudatlas.naming.AttributeName;
import stupaq.cloudatlas.naming.GlobalName;
import stupaq.cloudatlas.services.rmiserver.protocol.LocalClientProtocol;

import static com.google.common.base.Optional.of;

public class QueriesInstaller extends AbstractIdleService implements QueriesInstallerConfigKeys {
  private static final Log LOG = LogFactory.getLog(QueriesInstaller.class);
  private final File queriesFile;
  private final LocalClientProtocol client;
  private final ScheduledExecutorService executor;
  private HierarchicalINIConfiguration fileConfig;
  private CAConfiguration queries;
  private FileAlterationMonitor monitor;

  public QueriesInstaller(CAConfiguration configuration, LocalClientProtocol client,
      ScheduledExecutorService executor) {
    String source = configuration.getString(QUERIES_FILE);
    this.queriesFile = source == null ? null : new File(source);
    this.client = client;
    this.executor = executor;
  }

  @Override
  protected ScheduledExecutorService executor() {
    return executor;
  }

  @Override
  protected void startUp() throws Exception {
    if (queriesFile == null) {
      LOG.warn("Queries file not set, nothing to do");
      return;
    }
    // Try to retrieve configuration
    try {
      fileConfig = new HierarchicalINIConfiguration(queriesFile);
      fileConfig.setReloadingStrategy(new FileChangedReloadingStrategy());
      queries = new CAConfiguration(fileConfig);
    } catch (ConfigurationException e) {
      LOG.error("Failed to load queries file: " + queriesFile);
      throw e;
    }
    // Remove all queries if we were to handover the agent
    if (queries.getBoolean(REPLACE_ALL, REPLACE_ALL_DEFAULT)) {
      client.removeQuery(Optional.<AttributeName>absent(), Optional.<List<GlobalName>>absent());
    }
    // Set up file monitor
    FileAlterationObserver observer = new FileAlterationObserver(queriesFile);
    observer.addListener(new FileAlterationListenerAdaptor() {
      @Override
      public void onStart(FileAlterationObserver observer) {
        onFileChange(queriesFile);
      }

      @Override
      public void onFileChange(File file) {
        processConfig();
      }
    });
    monitor = new FileAlterationMonitor();
    monitor.addObserver(observer);
    monitor.start();
  }

  @Override
  protected void shutDown() throws Exception {
    if (queriesFile == null) {
      return;
    }
    monitor.stop();
    monitor = null;
    fileConfig = null;
    queries = null;
  }

  private void processConfig() {
    for (String section : fileConfig.getSections()) {
      processSection(section);
    }
  }

  private void processSection(String section) {
    if (section != null) {
      LOG.info("Processing query entry: " + section);
      AttributeName name = AttributeName.special(section);
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
}
