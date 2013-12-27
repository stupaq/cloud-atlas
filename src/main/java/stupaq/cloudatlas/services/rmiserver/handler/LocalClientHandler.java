package stupaq.cloudatlas.services.rmiserver.handler;

import com.google.common.base.Optional;
import com.google.common.eventbus.Subscribe;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.common.util.concurrent.SettableFuture;
import com.google.common.util.concurrent.Uninterruptibles;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.rmi.RemoteException;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import stupaq.cloudatlas.attribute.Attribute;
import stupaq.cloudatlas.attribute.values.CAContact;
import stupaq.cloudatlas.attribute.values.CAQuery;
import stupaq.cloudatlas.messaging.MessageBus;
import stupaq.cloudatlas.messaging.MessageListener.AbstractMessageListener;
import stupaq.cloudatlas.messaging.messages.AttributesUpdateMessage;
import stupaq.cloudatlas.messaging.messages.FallbackContactsMessage;
import stupaq.cloudatlas.messaging.messages.QueryRemovalMessage;
import stupaq.cloudatlas.messaging.messages.QueryUpdateMessage;
import stupaq.cloudatlas.messaging.messages.requests.Request;
import stupaq.cloudatlas.messaging.messages.requests.DumpZoneRequest;
import stupaq.cloudatlas.messaging.messages.requests.EntitiesValuesRequest;
import stupaq.cloudatlas.messaging.messages.requests.KnownZonesRequest;
import stupaq.cloudatlas.naming.AttributeName;
import stupaq.cloudatlas.naming.EntityName;
import stupaq.cloudatlas.naming.GlobalName;
import stupaq.cloudatlas.naming.LocalName;
import stupaq.cloudatlas.services.rmiserver.protocol.LocalClientProtocol;
import stupaq.cloudatlas.services.zonemanager.ZoneManagementInfo;
import stupaq.cloudatlas.services.zonemanager.hierarchy.ZoneHierarchy;
import stupaq.commons.util.concurrent.AsynchronousInvoker.DirectInvocation;

public class LocalClientHandler implements LocalClientProtocol {
  private static final Log LOG = LogFactory.getLog(LocalClientHandler.class);
  private final MessageBus bus;

  public LocalClientHandler(MessageBus bus) {
    this.bus = bus;
    // We're ready to cooperate
    bus.register(new HandlerListener());
  }

  @Override
  public void updateAttributes(GlobalName zone, List<Attribute> attributes) throws RemoteException {
    bus.post(new AttributesUpdateMessage(zone, attributes));
  }

  @Override
  public void setFallbackContacts(List<CAContact> contacts) throws RemoteException {
    bus.post(new FallbackContactsMessage(contacts));
  }

  @Override
  public ZoneManagementInfo getAttributes(GlobalName globalName) throws RemoteException {
    DumpZoneRequest request = new DumpZoneRequest(globalName);
    bus.post(request);
    return awaitResult(request).getZmi();
  }

  @Override
  public List<Attribute> getValues(List<EntityName> entities) throws RemoteException {
    EntitiesValuesRequest request = new EntitiesValuesRequest(entities);
    bus.post(request);
    return awaitResult(request).getList();
  }

  @Override
  public ZoneHierarchy<LocalName> getKnownZones() throws RemoteException {
    KnownZonesRequest request = new KnownZonesRequest();
    bus.post(request);
    return awaitResult(request).getZones();
  }

  @Override
  public void installQuery(Attribute<CAQuery> query, Optional<List<GlobalName>> zones)
      throws RemoteException {
    bus.post(new QueryUpdateMessage(query, zones));
  }

  @Override
  public void removeQuery(Optional<AttributeName> name, Optional<List<GlobalName>> zones)
      throws RemoteException {
    bus.post(new QueryRemovalMessage(name, zones));
  }

  private <Result> Result awaitResult(Request<SettableFuture<Result>> request)
      throws RemoteException {
    try {
      // If ZoneManager is not responding for a minute, then something is badly broken,
      // holding a bunch of threads (RMI callers) won't help for sure.
      return Uninterruptibles.getUninterruptibly(request.context(), 1, TimeUnit.MINUTES);
    } catch (ExecutionException e) {
      throw new RemoteException(e.getMessage());
    } catch (TimeoutException e) {
      LOG.error("RMI call timed out waiting for response", e);
      throw new RemoteException(e.getMessage());
    }
  }

  public static interface LocalClientResponse {
    Request<? extends SettableFuture> request();
  }

  private static interface HandlerContract {
    @Subscribe
    @DirectInvocation
    public void handleResponse(LocalClientResponse response);
  }

  private static class HandlerListener extends AbstractMessageListener implements HandlerContract {
    protected HandlerListener() {
      super(MoreExecutors.sameThreadExecutor(), HandlerContract.class);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void handleResponse(LocalClientResponse response) {
      response.request().context().set(response);
    }
  }
}
