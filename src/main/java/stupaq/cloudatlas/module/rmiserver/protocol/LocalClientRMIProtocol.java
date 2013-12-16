package stupaq.cloudatlas.module.rmiserver.protocol;

import java.rmi.Remote;
import java.rmi.RemoteException;

import stupaq.cloudatlas.bus.messages.AttributesUpdateMessage;
import stupaq.cloudatlas.bus.messages.FallbackContactsMessage;
import stupaq.cloudatlas.bus.messages.ZoneReportMessage;
import stupaq.cloudatlas.module.zonemanager.ZoneManagementInfo;
import stupaq.cloudatlas.naming.GlobalName;
import stupaq.compact.SerializableWrapper;

public interface LocalClientRMIProtocol extends Remote {

  public void updateAttributes(SerializableWrapper<AttributesUpdateMessage> attributes)
      throws RemoteException;

  // FIXME
  public SerializableWrapper<ZoneManagementInfo> getAttributes(
      SerializableWrapper<GlobalName> globalName) throws RemoteException;

  public void setFallbackContacts(SerializableWrapper<FallbackContactsMessage> attributes)
      throws RemoteException;

  public SerializableWrapper<ZoneReportMessage> getKnownZones();
}
