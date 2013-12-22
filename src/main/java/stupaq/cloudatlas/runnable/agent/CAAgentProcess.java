package stupaq.cloudatlas.runnable.agent;

import com.google.common.util.concurrent.AbstractIdleService;
import com.google.common.util.concurrent.Service;
import com.google.common.util.concurrent.ServiceManager;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.ArrayList;
import java.util.List;

import stupaq.cloudatlas.configuration.BootstrapConfiguration;
import stupaq.cloudatlas.services.rmiserver.RMIServer;
import stupaq.cloudatlas.services.zonemanager.ZoneManager;
import stupaq.cloudatlas.threading.PerServiceThreadModel;

import static stupaq.cloudatlas.configuration.BootstrapConfiguration.Builder;
import static stupaq.cloudatlas.configuration.ConfigurationDiscovery.forAgent;

@SuppressWarnings("unused")
public final class CAAgentProcess extends AbstractIdleService {
  private static final Log LOG = LogFactory.getLog(CAAgentProcess.class);
  private ServiceManager modules;

  public CAAgentProcess(String[] args) {
  }

  @Override
  protected void startUp() throws Exception {
    // Configuration for agent
    BootstrapConfiguration config =
        new Builder().configuration(forAgent()).threadModel(new PerServiceThreadModel()).create();
    // Create and start all services
    List<Service> services = new ArrayList<>();
    services.add(new RMIServer(config));
    services.add(new ZoneManager(config));
    modules = new ServiceManager(services);
    modules.startAsync().awaitHealthy();
  }

  @Override
  protected void shutDown() {
    modules.stopAsync().awaitStopped();
  }
}

