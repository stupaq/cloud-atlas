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

import stupaq.cloudatlas.configuration.BootstrapConfiguration;
import stupaq.cloudatlas.configuration.BootstrapConfiguration.Builder;
import stupaq.cloudatlas.naming.GlobalName;
import stupaq.cloudatlas.services.collector.AttributesCollector;
import stupaq.cloudatlas.services.installer.QueriesInstaller;
import stupaq.cloudatlas.services.rmiserver.protocol.LocalClientProtocol;
import stupaq.cloudatlas.services.scribe.AttributesScribe;
import stupaq.cloudatlas.threading.SingleThreadModel;

import static stupaq.cloudatlas.configuration.ConfigurationDiscovery.forLocalClient;
import static stupaq.cloudatlas.services.rmiserver.RMIServer.exportedName;

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
    // Establish RMI connection that will be shared by all services
    Registry registry = LocateRegistry.getRegistry(host);
    client = (LocalClientProtocol) registry.lookup(exportedName(LocalClientProtocol.class));
    // Configuration for client
    BootstrapConfiguration config = new Builder().configuration(forLocalClient()).zone(zone)
        .threadModel(new SingleThreadModel()).create();
    // Create and start all services
    List<Service> services = new ArrayList<>();
    services.add(new AttributesCollector(config, client));
    services.add(new AttributesScribe(config, client));
    services.add(new QueriesInstaller(config, client));
    manager = new ServiceManager(services);
    manager.startAsync().awaitHealthy();
  }

  @Override
  protected void shutDown() throws Exception {
    manager.stopAsync().awaitStopped();
    // The best what we can do about RMI client is to let it be finalized by the GC
    client = null;
  }
}
