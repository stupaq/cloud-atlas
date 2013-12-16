package stupaq.cloudatlas.runnable.client;

import com.google.common.base.Preconditions;
import com.google.common.util.concurrent.AbstractScheduledService;

import org.apache.commons.configuration.Configuration;
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
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import stupaq.cloudatlas.attribute.Attribute;
import stupaq.cloudatlas.attribute.AttributeName;
import stupaq.cloudatlas.attribute.values.CATime;
import stupaq.cloudatlas.bus.messages.AttributesUpdateRequest;
import stupaq.cloudatlas.module.rmiserver.RMIServer;
import stupaq.cloudatlas.module.rmiserver.protocol.LocalClientRMIProtocol;
import stupaq.cloudatlas.naming.GlobalName;
import stupaq.cloudatlas.serialization.CATypeRegistry;
import stupaq.compact.SerializableWrapper;

public class CAAttributesCollector extends AbstractScheduledService
    implements CAAttributesCollectorConfigKeys {
  private static final String CONFIGURATION_EXTENSION = ".ini";
  private static final Log LOG = LogFactory.getLog(CAAttributesCollector.class);
  private final GlobalName zone;
  private final String host;
  private final Configuration configuration;
  private LocalClientRMIProtocol client;

  public CAAttributesCollector(String host, GlobalName zone, File configSource)
      throws NotBoundException, RemoteException {
    this.host = host;
    this.zone = zone;
    // Find and load configuration
    if (configSource.isDirectory()) {
      configSource = new File(configSource,
          CAAttributesCollector.class.getSimpleName() + CONFIGURATION_EXTENSION);
    }
    FileConfiguration configuration = null;
    try {
      configuration = new HierarchicalINIConfiguration(configSource);
      configuration.setReloadingStrategy(new FileChangedReloadingStrategy());
    } catch (ConfigurationException e) {
      LOG.warn("Failed loading configuration, defaulting to empty one", e);
    }
    this.configuration = configuration;
  }

  @Override
  protected void startUp() throws RemoteException, NotBoundException {
    Registry registry = LocateRegistry.getRegistry(host);
    client = (LocalClientRMIProtocol) registry
        .lookup(RMIServer.exportedName(LocalClientRMIProtocol.class));
  }

  @Override
  protected void shutDown() {
    // The best what we can do about RMI client is to let it be finalized by the GC
    client = null;
  }

  private List<Attribute> collectAttributes() {
    List<Attribute> attributes = new ArrayList<>();
    // Obligatory attributes
    attributes.add(new Attribute<>(AttributeName.valueOf("timestamp"), CATime.now()));
    // FIXME
    return attributes;
  }

  @Override
  protected void runOneIteration() throws RemoteException {
    List<Attribute> attributes = collectAttributes();
    AttributesUpdateRequest message = new AttributesUpdateRequest(zone, attributes, false);
    client.updateAttributes(SerializableWrapper.wrap(message));
  }

  @Override
  protected Scheduler scheduler() {
    return new CustomScheduler() {
      @Override
      protected Schedule getNextSchedule() throws Exception {
        return new Schedule(configuration.getLong(PUSH_INTERVAL, PUSH_INTERVAL_DEFAULT),
            TimeUnit.MILLISECONDS);
      }
    };
  }

  public static void main(String[] args) {
    CATypeRegistry.registerCATypes();
    int status = 0;
    try {
      // Parse arguments
      Preconditions.checkArgument(args.length >= 2, "Missing arguments: zone, host");
      GlobalName zone = GlobalName.parse(args[0]);
      String host = args[1];
      File configSource = args.length > 2 ? new File(args[2]) : null;
      // Run the service
      CAAttributesCollector collector = new CAAttributesCollector(host, zone, configSource);
      collector.startAsync().awaitTerminated();
      if (collector.state() == State.FAILED) {
        LOG.error("Service exited abnormally", collector.failureCause());
        status = -2;
      }
    } catch (Exception e) {
      LOG.fatal("Exception in main() thread", e);
      status = -1;
    }
    System.exit(status);
  }
}
