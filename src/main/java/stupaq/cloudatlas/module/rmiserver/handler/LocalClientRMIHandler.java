package stupaq.cloudatlas.module.rmiserver.handler;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.rmi.RemoteException;

import stupaq.cloudatlas.bus.messages.AttributesUpdateMessage;
import stupaq.cloudatlas.bus.messages.FallbackContactsMessage;
import stupaq.cloudatlas.bus.messages.ZoneReportMessage;
import stupaq.cloudatlas.module.rmiserver.protocol.LocalClientRMIProtocol;
import stupaq.cloudatlas.module.zonemanager.ZoneManagementInfo;
import stupaq.cloudatlas.naming.GlobalName;
import stupaq.compact.SerializableWrapper;

public class LocalClientRMIHandler implements LocalClientRMIProtocol {
  private static final Log LOG = LogFactory.getLog(LocalClientRMIHandler.class);

  @Override
  public void updateAttributes(SerializableWrapper<AttributesUpdateMessage> message) {
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
  public void setFallbackContacts(SerializableWrapper<FallbackContactsMessage> message)
      throws RemoteException {
    if (LOG.isInfoEnabled()) {
      LOG.info("Received request to set fallback contacts: " + message.get());
    }
    // FIXME
  }

  @Override
  public SerializableWrapper<ZoneReportMessage> getKnownZones() {
    if (LOG.isInfoEnabled()) {
      LOG.info("Received zone report request");
    }
    // FIXME
    return null;
  }
}
