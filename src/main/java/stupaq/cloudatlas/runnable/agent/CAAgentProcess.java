package stupaq.cloudatlas.runnable.agent;

import com.google.common.base.Preconditions;
import com.google.common.util.concurrent.AbstractIdleService;
import com.google.common.util.concurrent.Service;
import com.google.common.util.concurrent.ServiceManager;

import org.apache.commons.configuration.BaseConfiguration;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.ArrayList;
import java.util.List;

import stupaq.cloudatlas.messaging.MessageBus;
import stupaq.cloudatlas.naming.GlobalName;
import stupaq.cloudatlas.services.rmiserver.RMIServer;
import stupaq.cloudatlas.services.zonemanager.ZoneManager;

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
    // Start all created services
    modules = new ServiceManager(createServices());
    modules.startAsync().awaitHealthy();
  }

  private Iterable<? extends Service> createServices() {
    // This bus glues all agent services
    MessageBus bus = new MessageBus();
    // Configuration for agent
    Configuration configuration = new BaseConfiguration();
    // All services
    List<Service> services = new ArrayList<>();
    services.add(new RMIServer(bus));
    services.add(new ZoneManager(configuration, bus, leafZone));
    return services;
  }

  @Override
  protected void shutDown() {
    modules.stopAsync().awaitStopped();
  }
}

