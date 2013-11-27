package stupaq.cloudatlas.module.rmiserver.protocol;

import java.rmi.Remote;
import java.rmi.RemoteException;

import stupaq.cloudatlas.bus.messages.AttributesUpdateMessage;
import stupaq.compact.SerializableWrapper;

public interface AttributeCollectionProtocol extends Remote {

  public void collect(SerializableWrapper<AttributesUpdateMessage> attributes)
      throws RemoteException;
}
