package stupaq.cloudatlas.service.rmiserver.protocol;

import java.rmi.Remote;
import java.rmi.RemoteException;

import stupaq.cloudatlas.messaging.messages.AttributesUpdateRequest;
import stupaq.cloudatlas.messaging.messages.FallbackContactsRequest;
import stupaq.cloudatlas.messaging.messages.ZoneReportResponse;
import stupaq.cloudatlas.naming.GlobalName;
import stupaq.cloudatlas.service.zonemanager.ZoneManagementInfo;
import stupaq.compact.SerializableWrapper;

public interface LocalClientRMIProtocol extends Remote {

  public void updateAttributes(SerializableWrapper<AttributesUpdateRequest> attributes)
      throws RemoteException;

  // FIXME
  public SerializableWrapper<ZoneManagementInfo> getAttributes(
      SerializableWrapper<GlobalName> globalName) throws RemoteException;

  public void setFallbackContacts(SerializableWrapper<FallbackContactsRequest> attributes)
      throws RemoteException;

  public SerializableWrapper<ZoneReportResponse> getKnownZones() throws RemoteException;
}
