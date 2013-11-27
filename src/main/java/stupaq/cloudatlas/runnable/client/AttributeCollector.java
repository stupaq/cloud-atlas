package stupaq.cloudatlas.runnable.client;

import com.google.common.base.Preconditions;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.List;

import stupaq.cloudatlas.attribute.Attribute;
import stupaq.cloudatlas.attribute.AttributeName;
import stupaq.cloudatlas.attribute.values.CATime;
import stupaq.cloudatlas.bus.messages.AttributesUpdateMessage;
import stupaq.cloudatlas.module.rmiserver.RMIServer;
import stupaq.cloudatlas.module.rmiserver.protocol.AttributeCollectionProtocol;
import stupaq.cloudatlas.naming.GlobalName;
import stupaq.cloudatlas.serialization.CATypeRegistry;
import stupaq.compact.SerializableWrapper;

public class AttributeCollector {
  private static final Log LOG = LogFactory.getLog(AttributeCollector.class);

  private static AttributeCollectionProtocol createClient(String host)
      throws RemoteException, NotBoundException {
    try {
      Registry registry = LocateRegistry.getRegistry(host);
      return (AttributeCollectionProtocol) registry
          .lookup(RMIServer.exportedName(AttributeCollectionProtocol.class));
    } catch (Exception e) {
      LOG.fatal("Failed to create client", e);
      throw e;
    }
  }

  private static List<Attribute> collectAttributes(GlobalName zone) {
    List<Attribute> attributes = new ArrayList<>();
    // Obligatory attributes
    attributes.add(new Attribute<>(AttributeName.valueOf("timestamp"), CATime.now()));
    // FIXME
    return attributes;
  }

  public static void main(String[] args) {
    CATypeRegistry.registerCATypes();
    try {
      // Arguments
      Preconditions.checkArgument(args.length >= 2, "Missing arguments: zone, host");
      GlobalName zone = GlobalName.parse(args[0]);
      String host = args[1];
      // Collect attributes
      List<Attribute> attributes = collectAttributes(zone);
      // Prepare message
      AttributesUpdateMessage message = new AttributesUpdateMessage(zone, attributes, false);
      // Send
      createClient(host).collect(new SerializableWrapper<>(message));
    } catch (Exception e) {
      LOG.fatal("Exception in main() thread", e);
      System.exit(-1);
    }
  }
}
