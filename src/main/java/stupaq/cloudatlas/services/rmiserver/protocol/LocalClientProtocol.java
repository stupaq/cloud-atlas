package stupaq.cloudatlas.services.rmiserver.protocol;

import java.rmi.Remote;
import java.rmi.RemoteException;

import stupaq.cloudatlas.messaging.messages.AttributesUpdateMessage;
import stupaq.cloudatlas.messaging.messages.EntitiesValuesRequest;
import stupaq.cloudatlas.messaging.messages.EntitiesValuesResponse;
import stupaq.cloudatlas.messaging.messages.FallbackContactsMessage;
import stupaq.cloudatlas.messaging.messages.KnownZonesResponse;
import stupaq.cloudatlas.naming.GlobalName;
import stupaq.cloudatlas.services.zonemanager.ZoneManagementInfo;
import stupaq.compact.SerializableWrapper;

public interface LocalClientProtocol extends Remote {

  public void updateAttributes(SerializableWrapper<AttributesUpdateMessage> attributes)
      throws RemoteException;

  public SerializableWrapper<ZoneManagementInfo> getAttributes(
      SerializableWrapper<GlobalName> globalName) throws RemoteException;

  public SerializableWrapper<EntitiesValuesResponse> getValues(
      SerializableWrapper<EntitiesValuesRequest> request) throws RemoteException;

  public void setFallbackContacts(SerializableWrapper<FallbackContactsMessage> attributes)
      throws RemoteException;

  public SerializableWrapper<KnownZonesResponse> getKnownZones() throws RemoteException;
}
