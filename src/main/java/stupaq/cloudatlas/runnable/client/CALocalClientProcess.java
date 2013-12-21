package stupaq.cloudatlas.runnable.client;

import com.google.common.base.Preconditions;
import com.google.common.util.concurrent.AbstractIdleService;
import com.google.common.util.concurrent.Service;
import com.google.common.util.concurrent.ServiceManager;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;

import stupaq.cloudatlas.configuration.CAConfiguration;
import stupaq.cloudatlas.configuration.ConfigurationDiscovery;
import stupaq.cloudatlas.naming.GlobalName;
import stupaq.cloudatlas.services.collector.AttributesCollector;
import stupaq.cloudatlas.services.installer.QueriesInstaller;
import stupaq.cloudatlas.services.rmiserver.RMIServer;
import stupaq.cloudatlas.services.rmiserver.protocol.LocalClientProtocol;
import stupaq.cloudatlas.services.scribe.AttributesScribe;
import stupaq.commons.util.concurrent.SingleThreadedExecutor;

@SuppressWarnings("unused")
public class CALocalClientProcess extends AbstractIdleService {
  private static final Log LOG = LogFactory.getLog(CALocalClientProcess.class);
  private final String host;
  private final GlobalName zone;
  private LocalClientProtocol client;
  private ScheduledExecutorService executor;
  private ServiceManager manager;

  public CALocalClientProcess(String[] args) {
    Preconditions.checkArgument(args.length >= 2, "Missing arguments: leaf zone, agent host");
    zone = GlobalName.parse(args[0]);
    host = args[1];
  }

  @Override
  protected void startUp() throws NotBoundException, RemoteException {
    // Find and load configuration
    CAConfiguration config = ConfigurationDiscovery.forLocalClient();
    // Establish RMI connection that will be shared by all services
    Registry registry = LocateRegistry.getRegistry(host);
    client =
        (LocalClientProtocol) registry.lookup(RMIServer.exportedName(LocalClientProtocol.class));
    // Create shared executor
    executor = new SingleThreadedExecutor();
    // Create and start all services
    List<Service> services = new ArrayList<>();
    services.add(new AttributesCollector(zone, config, client, executor));
    services.add(new AttributesScribe(config, client, executor));
    services.add(new QueriesInstaller(config, client, executor));
    manager = new ServiceManager(services);
    manager.startAsync().awaitHealthy();
  }

  @Override
  protected void shutDown() throws Exception {
    manager.stopAsync().awaitStopped();
    executor.shutdownNow();
    executor = null;
    // The best what we can do about RMI client is to let it be finalized by the GC
    client = null;
  }
}
