package stupaq.cloudatlas.runnable.client;

import com.google.common.base.Preconditions;
import com.google.common.util.concurrent.AbstractIdleService;
import com.google.common.util.concurrent.ServiceManager;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import stupaq.cloudatlas.configuration.BootstrapConfiguration;
import stupaq.cloudatlas.configuration.BootstrapConfiguration.Builder;
import stupaq.cloudatlas.configuration.ServicesList;
import stupaq.cloudatlas.services.collector.AttributesCollector;
import stupaq.cloudatlas.services.installer.QueriesInstaller;
import stupaq.cloudatlas.services.rmiserver.RMIServerConfigKeys;
import stupaq.cloudatlas.services.scribe.AttributesScribe;
import stupaq.cloudatlas.threading.SingleThreadModel;

@SuppressWarnings("unused")
public class CALocalClientProcess extends AbstractIdleService implements RMIServerConfigKeys {
  private static final Log LOG = LogFactory.getLog(CALocalClientProcess.class);
  private final List<File> configPaths = new ArrayList<>();
  private ServiceManager manager;

  public CALocalClientProcess(String[] args) {
    Preconditions.checkArgument(args.length >= 1, "Missing config file(s)");
    for (String path : args) {
      configPaths.add(new File(path));
    }
  }

  @Override
  protected void startUp() throws Exception {
    ServicesList services = new ServicesList();
    for (File configPath : configPaths) {
      // Configuration for client
      BootstrapConfiguration config =
          new Builder().configFile(configPath, CALocalClientProcess.class)
              .threadModel(new SingleThreadModel()).create();
      // Create and start all services
      services.addWith(config, AttributesCollector.class);
      services.addWith(config, AttributesScribe.class);
      services.addWith(config, QueriesInstaller.class);
    }
    manager = new ServiceManager(services);
    manager.startAsync().awaitHealthy();
  }

  @Override
  protected void shutDown() throws Exception {
    manager.stopAsync().awaitStopped();
  }
}
