package stupaq.cloudatlas.services.collector;

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
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import stupaq.cloudatlas.attribute.Attribute;
import stupaq.cloudatlas.naming.AttributeName;
import stupaq.cloudatlas.attribute.values.CADouble;
import stupaq.cloudatlas.attribute.values.CAInteger;
import stupaq.cloudatlas.attribute.values.CASet;
import stupaq.cloudatlas.attribute.values.CAString;
import stupaq.cloudatlas.configuration.CAConfiguration;
import stupaq.cloudatlas.messaging.messages.AttributesUpdateMessage;
import stupaq.cloudatlas.naming.GlobalName;
import stupaq.cloudatlas.query.typecheck.TypeInfo;
import stupaq.cloudatlas.services.rmiserver.protocol.LocalClientProtocol;
import stupaq.compact.SerializableWrapper;

public class AttributesCollector extends AbstractScheduledService
    implements AttributesCollectorConfigKeys {
  private static final Log LOG = LogFactory.getLog(AttributesCollector.class);
  private final GlobalName zone;
  private final CAConfiguration configuration;
  private final ScheduledExecutorService executor;
  private final LocalClientProtocol client;

  public AttributesCollector(GlobalName zone, CAConfiguration configuration,
      LocalClientProtocol client, ScheduledExecutorService executor)
      throws NotBoundException, RemoteException {
    this.zone = zone;
    this.client = client;
    this.configuration = configuration;
    this.executor = executor;
  }

  @Override
  protected void runOneIteration() throws IOException {
    List<Attribute> attributes = collectAttributes();
    AttributesUpdateMessage message = new AttributesUpdateMessage(zone, attributes, false);
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

  @Override
  protected ScheduledExecutorService executor() {
    return executor;
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
          attributes.add(
              new Attribute<>(AttributeName.valueOf(name), new CAInteger(collected.getLong(name))));
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
}
