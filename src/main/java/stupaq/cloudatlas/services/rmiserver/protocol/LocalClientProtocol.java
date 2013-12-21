package stupaq.cloudatlas.services.rmiserver.protocol;

import com.google.common.base.Optional;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

import javax.annotation.ParametersAreNonnullByDefault;

import stupaq.cloudatlas.attribute.Attribute;
import stupaq.cloudatlas.attribute.values.CAContact;
import stupaq.cloudatlas.attribute.values.CAQuery;
import stupaq.cloudatlas.naming.AttributeName;
import stupaq.cloudatlas.naming.EntityName;
import stupaq.cloudatlas.naming.GlobalName;
import stupaq.cloudatlas.naming.LocalName;
import stupaq.cloudatlas.services.zonemanager.ZoneManagementInfo;
import stupaq.cloudatlas.services.zonemanager.hierarchy.ZoneHierarchy;

@ParametersAreNonnullByDefault
public interface LocalClientProtocol extends Remote {

  public void updateAttributes(GlobalName zone, List<Attribute> attributes, boolean override)
      throws RemoteException;

  public void setFallbackContacts(List<CAContact> attributes) throws RemoteException;

  public ZoneManagementInfo getAttributes(GlobalName globalName) throws RemoteException;

  public List<Attribute> getValues(List<EntityName> request) throws RemoteException;

  public ZoneHierarchy<LocalName> getKnownZones() throws RemoteException;

  public void installQuery(Attribute<CAQuery> query, Optional<List<GlobalName>> zones)
      throws RemoteException;

  public void removeQuery(Optional<AttributeName> name, Optional<List<GlobalName>> zones)
      throws RemoteException;
}
