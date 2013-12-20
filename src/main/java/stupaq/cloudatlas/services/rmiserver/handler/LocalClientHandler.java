package stupaq.cloudatlas.services.rmiserver.handler;

import com.google.common.eventbus.Subscribe;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.common.util.concurrent.SettableFuture;
import com.google.common.util.concurrent.Uninterruptibles;

import java.rmi.RemoteException;
import java.util.List;
import java.util.concurrent.ExecutionException;

import stupaq.cloudatlas.attribute.Attribute;
import stupaq.cloudatlas.attribute.values.CAContact;
import stupaq.cloudatlas.messaging.MessageBus;
import stupaq.cloudatlas.messaging.MessageListener.AbstractMessageListener;
import stupaq.cloudatlas.messaging.Request;
import stupaq.cloudatlas.messaging.messages.AttributesUpdateMessage;
import stupaq.cloudatlas.messaging.messages.DumpZoneRequest;
import stupaq.cloudatlas.messaging.messages.EntitiesValuesRequest;
import stupaq.cloudatlas.messaging.messages.FallbackContactsMessage;
import stupaq.cloudatlas.messaging.messages.KnownZonesRequest;
import stupaq.cloudatlas.naming.EntityName;
import stupaq.cloudatlas.naming.GlobalName;
import stupaq.cloudatlas.naming.LocalName;
import stupaq.cloudatlas.services.rmiserver.protocol.LocalClientProtocol;
import stupaq.cloudatlas.services.zonemanager.ZoneManagementInfo;
import stupaq.cloudatlas.services.zonemanager.hierarchy.ZoneHierarchy;
import stupaq.commons.util.concurrent.AsynchronousInvoker.DirectInvocation;

public class LocalClientHandler implements LocalClientProtocol {
  private final MessageBus bus;

  public LocalClientHandler(MessageBus bus) {
    this.bus = bus;
    // We're ready to cooperate
    bus.register(new HandlerListener());
  }

  @Override
  public void updateAttributes(GlobalName zone, List<Attribute> attributes, boolean override)
      throws RemoteException {
    bus.post(new AttributesUpdateMessage(zone, attributes, override));
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

  private <Result> Result awaitResult(Request<SettableFuture<Result>> request)
      throws RemoteException {
    try {
      return Uninterruptibles.getUninterruptibly(request.context());
    } catch (ExecutionException e) {
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
