package stupaq.cloudatlas.runnable.agent;

import com.google.common.base.Preconditions;
import com.google.common.util.concurrent.AbstractIdleService;
import com.google.common.util.concurrent.ServiceManager;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import stupaq.cloudatlas.configuration.BootstrapConfiguration;
import stupaq.cloudatlas.configuration.ServicesList;
import stupaq.cloudatlas.services.busybody.Busybody;
import stupaq.cloudatlas.services.rmiserver.RMIServer;
import stupaq.cloudatlas.services.zonemanager.ZoneManager;
import stupaq.cloudatlas.threading.PerServiceThreadModel;

import static stupaq.cloudatlas.configuration.BootstrapConfiguration.Builder;

@SuppressWarnings("unused")
public final class CAAgentProcess extends AbstractIdleService {
  private static final Log LOG = LogFactory.getLog(CAAgentProcess.class);
  private final List<File> configPaths = new ArrayList<>();
  private ServiceManager modules;

  public CAAgentProcess(String[] args) {
    Preconditions.checkArgument(args.length >= 1, "Missing config file(s)");
    for (String path : args) {
      configPaths.add(new File(path));
    }
  }

  @Override
  protected void startUp() throws Exception {
    ServicesList services = new ServicesList();
    for (File configPath : configPaths) {
      // Configuration for agent
      BootstrapConfiguration config = new Builder().configFile(configPath, CAAgentProcess.class)
          .threadModel(new PerServiceThreadModel()).create();
      // Create and start all services
      services.addWith(config, RMIServer.class);
      services.addWith(config, ZoneManager.class);
      services.addWith(config, Busybody.class);
    }
    modules = new ServiceManager(services);
    modules.startAsync().awaitHealthy();
  }

  @Override
  protected void shutDown() {
    modules.stopAsync().awaitStopped();
  }
}

