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
import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import stupaq.cloudatlas.attribute.Attribute;
import stupaq.cloudatlas.attribute.AttributeName;
import stupaq.cloudatlas.attribute.values.CADouble;
import stupaq.cloudatlas.attribute.values.CAInteger;
import stupaq.cloudatlas.attribute.values.CASet;
import stupaq.cloudatlas.attribute.values.CAString;
import stupaq.cloudatlas.bus.messages.AttributesUpdateRequest;
import stupaq.cloudatlas.module.rmiserver.RMIServer;
import stupaq.cloudatlas.module.rmiserver.protocol.LocalClientRMIProtocol;
import stupaq.cloudatlas.naming.GlobalName;
import stupaq.cloudatlas.query.typecheck.TypeInfo;
import stupaq.cloudatlas.serialization.CATypeRegistry;
import stupaq.compact.SerializableWrapper;

public class CAAttributesCollector extends AbstractScheduledService
    implements CAAttributesCollectorConfigKeys {
  private static final Log LOG = LogFactory.getLog(CAAttributesCollector.class);
  private static final List<String> ATTRIBUTES_DOUBLE = Arrays.asList("cpu_load");
  private static final List<String> ATTRIBUTES_LONG = Arrays.asList("free_disk",
      "total_disk",
      "free_ram",
      "total_ram",
      "free_swap",
      "total_swap",
      "num_processes",
      "num_cores",
      "logged_users");
  private static final List<String> ATTRIBUTES_STRING = Arrays.asList("kernel_ver");
  private static final List<String> ATTRIBUTES_SET_STRING = Arrays.asList("dns_names");
  private static final String CONFIGURATION_EXTENSION = ".ini";
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

  private List<Attribute> collectAttributes() throws IOException {
    try {
      Process process = Runtime.getRuntime().exec(configuration.getString(SCRIPT, SCRIPT_DEFAULT));
      FileConfiguration collected = new HierarchicalINIConfiguration();
      collected.load(process.getInputStream());
      if (process.waitFor() != 0) {
        throw new IOException("Script failed with exit code: " + process.exitValue());
      }
      // Rewrite known attributes
      List<Attribute> attributes = new ArrayList<>();
      for (String name : ATTRIBUTES_DOUBLE) {
        if (collected.containsKey(name)) {
          attributes.add(new Attribute<>(AttributeName.valueOf(name),
              new CADouble(collected.getDouble(name))));
        }
      }
      for (String name : ATTRIBUTES_LONG) {
        if (collected.containsKey(name)) {
          attributes.add(new Attribute<>(AttributeName.valueOf(name),
              new CAInteger(collected.getLong(name))));
        }
      }
      for (String name : ATTRIBUTES_STRING) {
        if (collected.containsKey(name)) {
          attributes.add(new Attribute<>(AttributeName.valueOf(name),
              new CAString(collected.getString(name))));
        }
      }
      for (String name : ATTRIBUTES_SET_STRING) {
        if (collected.containsKey(name)) {
          List<CAString> list = new ArrayList<>();
          for (String element : collected.getStringArray(name)) {
            list.add(new CAString(element));
          }
          attributes.add(new Attribute<>(AttributeName.valueOf(name),
              new CASet<>(TypeInfo.of(CAString.class), list)));
        }
      }
      return attributes;
    } catch (ConfigurationException e) {
      LOG.fatal("Attributes collection script failed ");
      throw new IOException(e);
    } catch (InterruptedException e) {
      LOG.fatal("Interrupter while executing collection script");
      throw new IOException(e);
    }
  }

  @Override
  protected void runOneIteration() throws IOException {
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
