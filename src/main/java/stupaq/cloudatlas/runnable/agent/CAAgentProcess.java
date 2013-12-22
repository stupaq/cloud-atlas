package stupaq.cloudatlas.runnable.agent;

import com.google.common.base.Preconditions;
import com.google.common.util.concurrent.AbstractIdleService;
import com.google.common.util.concurrent.Service;
import com.google.common.util.concurrent.ServiceManager;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.ArrayList;
import java.util.List;

import stupaq.cloudatlas.configuration.BootstrapConfiguration;
import stupaq.cloudatlas.naming.GlobalName;
import stupaq.cloudatlas.services.rmiserver.RMIServer;
import stupaq.cloudatlas.services.zonemanager.ZoneManager;

import static stupaq.cloudatlas.configuration.BootstrapConfiguration.Builder;
import static stupaq.cloudatlas.configuration.ConfigurationDiscovery.forAgent;

@SuppressWarnings("unused")
public final class CAAgentProcess extends AbstractIdleService {
  private static final Log LOG = LogFactory.getLog(CAAgentProcess.class);
  private final GlobalName leafZone;
  private ServiceManager modules;

  public CAAgentProcess(String[] args) {
    Preconditions.checkArgument(args.length >= 1, "Missing arguments: leaf zone");
    leafZone = GlobalName.parse(args[0]);
  }

  @Override
  protected void startUp() throws Exception {
    // Configuration for agent
    BootstrapConfiguration config =
        new Builder().configuration(forAgent(leafZone)).leafZone(leafZone).create();
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

