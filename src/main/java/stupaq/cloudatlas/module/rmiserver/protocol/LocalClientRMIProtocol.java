package stupaq.cloudatlas.module.rmiserver.protocol;

import java.rmi.Remote;
import java.rmi.RemoteException;

import stupaq.cloudatlas.bus.messages.AttributesUpdateRequest;
import stupaq.cloudatlas.bus.messages.FallbackContactsRequest;
import stupaq.cloudatlas.bus.messages.ZoneReportResponse;
import stupaq.cloudatlas.module.zonemanager.ZoneManagementInfo;
import stupaq.cloudatlas.naming.GlobalName;
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
