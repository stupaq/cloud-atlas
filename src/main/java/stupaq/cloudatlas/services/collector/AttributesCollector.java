package stupaq.cloudatlas.services.collector;

import com.google.common.base.Function;
import com.google.common.util.concurrent.AbstractScheduledService;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.FileConfiguration;
import org.apache.commons.configuration.HierarchicalINIConfiguration;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;

import stupaq.cloudatlas.attribute.Attribute;
import stupaq.cloudatlas.attribute.values.CAContact;
import stupaq.cloudatlas.attribute.values.CADouble;
import stupaq.cloudatlas.attribute.values.CAInteger;
import stupaq.cloudatlas.attribute.values.CASet;
import stupaq.cloudatlas.attribute.values.CAString;
import stupaq.cloudatlas.configuration.BootstrapConfiguration;
import stupaq.cloudatlas.configuration.StartIfPresent;
import stupaq.cloudatlas.naming.AttributeName;
import stupaq.cloudatlas.naming.GlobalName;
import stupaq.cloudatlas.services.rmiserver.RMIServerConfigKeys;
import stupaq.cloudatlas.services.rmiserver.protocol.LocalClientProtocol;
import stupaq.cloudatlas.services.zonemanager.builtins.BuiltinAttributesConfigKeys;
import stupaq.commons.util.concurrent.FastStartScheduler;
import stupaq.commons.util.concurrent.SingleThreadedExecutor;

import static com.google.common.collect.FluentIterable.from;
import static java.util.Arrays.asList;
import static stupaq.cloudatlas.query.typecheck.TypeInfo.of;
import static stupaq.cloudatlas.services.rmiserver.RMIServer.createClient;

@StartIfPresent(section = "collector")
public class AttributesCollector extends AbstractScheduledService
    implements AttributesCollectorConfigKeys, BuiltinAttributesConfigKeys, RMIServerConfigKeys {
  private static final Log LOG = LogFactory.getLog(AttributesCollector.class);
  private final BootstrapConfiguration config;
  private final SingleThreadedExecutor executor;
  private final GlobalName zone;
  private LocalClientProtocol client;

  public AttributesCollector(BootstrapConfiguration config) {
    config.mustContain(ZONE_NAME);
    this.config = config;
    this.zone = config.getGlobalName(ZONE_NAME);
    this.executor = config.threadModel().singleThreaded(AttributesCollector.class);
  }

  @Override
  protected void startUp() throws NotBoundException, RemoteException {
    client = createClient(LocalClientProtocol.class, config);
  }

  @Override
  protected void runOneIteration() throws IOException {
    client.updateAttributes(zone, collectAttributes());
    client.setFallbackContacts(collectFallbackContacts());
  }

  @Override
  protected void shutDown() {
    config.threadModel().free(executor);
    client = null;
  }

  @Override
  protected Scheduler scheduler() {
    return new FastStartScheduler() {
      @Override
      protected long getNextDelayMs() throws Exception {
        return config.getLong(PUSH_INTERVAL, PUSH_INTERVAL_DEFAULT);
      }
    };
  }

  @Override
  protected ScheduledExecutorService executor() {
    return executor;
  }

  private List<CAContact> collectFallbackContacts() {
    if (config.containsKey(FALLBACK_CONTACTS)) {
      return from(asList(config.getStringArray(FALLBACK_CONTACTS))).transform(
          new Function<String, CAContact>() {
            @Override
            public CAContact apply(String input) {
              return new CAContact(input);
            }
          }).toList();
    } else {
      return Collections.emptyList();
    }
  }

  private List<Attribute> collectAttributes() throws IOException {
    try {
      Process process = Runtime.getRuntime().exec(config.getString(SCRIPT, SCRIPT_DEFAULT));
      FileConfiguration collected = new HierarchicalINIConfiguration();
      collected.load(process.getInputStream());
      if (process.waitFor() != 0) {
        throw new IOException("Script failed with exit code: " + process.exitValue());
      }
      // Rewrite known attributes
      List<Attribute> attributes = new ArrayList<>();
      for (String name : ATTRIBUTES_DOUBLE) {
        if (collected.containsKey(name)) {
          attributes.add(new Attribute<>(AttributeName.fromString(name),
              new CADouble(collected.getDouble(name))));
        }
      }
      for (String name : ATTRIBUTES_LONG) {
        if (collected.containsKey(name)) {
          attributes.add(new Attribute<>(AttributeName.fromString(name),
              new CAInteger(collected.getLong(name))));
        }
      }
      for (String name : ATTRIBUTES_STRING) {
        if (collected.containsKey(name)) {
          attributes.add(new Attribute<>(AttributeName.fromString(name),
              new CAString(collected.getString(name))));
        }
      }
      for (String name : ATTRIBUTES_SET_STRING) {
        if (collected.containsKey(name)) {
          List<CAString> list = new ArrayList<>();
          for (String element : collected.getStringArray(name)) {
            list.add(new CAString(element));
          }
          attributes.add(new Attribute<>(AttributeName.fromString(name),
              new CASet<>(of(CAString.class), list)));
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
}
