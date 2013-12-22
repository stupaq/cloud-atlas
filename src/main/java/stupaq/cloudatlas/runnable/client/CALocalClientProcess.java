package stupaq.cloudatlas.runnable.client;

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
  private static final String RMI_HOST = "rmi_host";
  private LocalClientProtocol client;
  private ScheduledExecutorService executor;
  private ServiceManager manager;

  public CALocalClientProcess(String[] args) {
  }

  @Override
  protected void startUp() throws NotBoundException, RemoteException {
    // Configuration for client
    BootstrapConfiguration config =
        new Builder().configuration(forLocalClient()).threadModel(new SingleThreadModel()).create();
    // Establish RMI connection that will be shared by all services
    Registry registry = LocateRegistry.getRegistry(config.getString(RMI_HOST));
    client = (LocalClientProtocol) registry.lookup(exportedName(LocalClientProtocol.class));
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
