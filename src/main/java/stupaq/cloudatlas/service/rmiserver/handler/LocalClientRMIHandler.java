package stupaq.cloudatlas.service.rmiserver.handler;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.rmi.RemoteException;

import stupaq.cloudatlas.bus.messages.AttributesUpdateRequest;
import stupaq.cloudatlas.bus.messages.FallbackContactsRequest;
import stupaq.cloudatlas.bus.messages.ZoneReportResponse;
import stupaq.cloudatlas.service.rmiserver.protocol.LocalClientRMIProtocol;
import stupaq.cloudatlas.service.zonemanager.ZoneManagementInfo;
import stupaq.cloudatlas.naming.GlobalName;
import stupaq.compact.SerializableWrapper;

public class LocalClientRMIHandler implements LocalClientRMIProtocol {
  private static final Log LOG = LogFactory.getLog(LocalClientRMIHandler.class);

  @Override
  public void updateAttributes(SerializableWrapper<AttributesUpdateRequest> message) {
    if (LOG.isInfoEnabled()) {
      LOG.info("Received attributes update: " + message.get());
    }
    // FIXME
  }

  @Override
  public SerializableWrapper<ZoneManagementInfo> getAttributes(
      SerializableWrapper<GlobalName> globalName) throws RemoteException {
    if (LOG.isInfoEnabled()) {
      LOG.info("Asked for attributes for zone: " + globalName.get());
    }
    // FIXME
    return null;
  }

  @Override
  public void setFallbackContacts(SerializableWrapper<FallbackContactsRequest> message)
      throws RemoteException {
    if (LOG.isInfoEnabled()) {
      LOG.info("Received request to set fallback contacts: " + message.get());
    }
    // FIXME
  }

  @Override
  public SerializableWrapper<ZoneReportResponse> getKnownZones() {
    if (LOG.isInfoEnabled()) {
      LOG.info("Received zone report request");
    }
    // FIXME
    return null;
  }
}
