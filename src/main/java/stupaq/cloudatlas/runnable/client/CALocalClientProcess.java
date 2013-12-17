package stupaq.cloudatlas.runnable.client;

import com.google.common.base.Preconditions;
import com.google.common.util.concurrent.AbstractIdleService;
import com.google.common.util.concurrent.ServiceManager;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.FileConfiguration;
import org.apache.commons.configuration.HierarchicalINIConfiguration;
import org.apache.commons.configuration.reloading.FileChangedReloadingStrategy;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.File;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Arrays;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import stupaq.cloudatlas.service.collector.AttributesCollector;
import stupaq.cloudatlas.service.rmiserver.RMIServer;
import stupaq.cloudatlas.service.rmiserver.protocol.LocalClientRMIProtocol;
import stupaq.cloudatlas.naming.GlobalName;

@SuppressWarnings("unused")
public class CALocalClientProcess extends AbstractIdleService {
  private static final Log LOG = LogFactory.getLog(CALocalClientProcess.class);
  private static final String CONFIGURATION_EXTENSION = ".ini";
  private static final String CONFIGURATION_DEFAULT_DIRECTORY = "config/";
  private final String host;
  private final GlobalName zone;
  private File configSource;
  private LocalClientRMIProtocol client;
  private ScheduledExecutorService executor;
  private ServiceManager manager;

  public CALocalClientProcess(String[] args) {
    Preconditions.checkArgument(args.length >= 2, "Missing arguments: leaf zone, agent host");
    zone = GlobalName.parse(args[0]);
    host = args[1];
    configSource = new File(args.length > 2 ? args[2] : CONFIGURATION_DEFAULT_DIRECTORY);
  }

  @Override
  protected void startUp() throws NotBoundException, RemoteException {
    // Find and load configuration
    if (configSource.isDirectory()) {
      configSource = new File(configSource,
          CALocalClientProcess.class.getSimpleName() + CONFIGURATION_EXTENSION);
    }
    FileConfiguration configuration;
    try {
      configuration = new HierarchicalINIConfiguration(configSource);
      configuration.setReloadingStrategy(new FileChangedReloadingStrategy());
    } catch (ConfigurationException e) {
      LOG.warn("Failed loading configuration, defaulting to empty one", e);
      configuration = new HierarchicalINIConfiguration();
    }
    // Establish RMI connection that will be shared by all services
    Registry registry = LocateRegistry.getRegistry(host);
    client = (LocalClientRMIProtocol) registry
        .lookup(RMIServer.exportedName(LocalClientRMIProtocol.class));
    // Create shared executor
    executor = Executors.newSingleThreadScheduledExecutor();
    // Create and start all services
    manager =
        new ServiceManager(Arrays.asList(new AttributesCollector(zone, configuration, client, executor)));
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
