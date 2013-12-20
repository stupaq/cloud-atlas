package stupaq.cloudatlas.services.rmiserver.protocol;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

import stupaq.cloudatlas.attribute.Attribute;
import stupaq.cloudatlas.attribute.values.CAContact;
import stupaq.cloudatlas.naming.EntityName;
import stupaq.cloudatlas.naming.GlobalName;
import stupaq.cloudatlas.naming.LocalName;
import stupaq.cloudatlas.services.zonemanager.ZoneManagementInfo;
import stupaq.cloudatlas.services.zonemanager.hierarchy.ZoneHierarchy;
import stupaq.compact.SerializableWrapper;

public interface LocalClientProtocol extends Remote {

  public void updateAttributes(GlobalName zone, List<Attribute> attributes, boolean override)
      throws RemoteException;

  public void setFallbackContacts(List<SerializableWrapper<CAContact>> attributes)
      throws RemoteException;

  public ZoneManagementInfo getAttributes(GlobalName globalName) throws RemoteException;

  public List<Attribute> getValues(List<EntityName> request) throws RemoteException;

  public ZoneHierarchy<LocalName> getKnownZones() throws RemoteException;
}
