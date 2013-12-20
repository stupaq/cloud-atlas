package stupaq.cloudatlas.services.rmiserver.handler;

import com.google.common.eventbus.Subscribe;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.common.util.concurrent.SettableFuture;
import com.google.common.util.concurrent.Uninterruptibles;

import java.rmi.RemoteException;
import java.util.concurrent.ExecutionException;

import stupaq.cloudatlas.messaging.MessageBus;
import stupaq.cloudatlas.messaging.MessageListener.AbstractMessageListener;
import stupaq.cloudatlas.messaging.Request;
import stupaq.cloudatlas.messaging.messages.AttributesUpdateMessage;
import stupaq.cloudatlas.messaging.messages.DumpZoneRequest;
import stupaq.cloudatlas.messaging.messages.DumpZoneResponse;
import stupaq.cloudatlas.messaging.messages.EntitiesValuesRequest;
import stupaq.cloudatlas.messaging.messages.EntitiesValuesResponse;
import stupaq.cloudatlas.messaging.messages.FallbackContactsMessage;
import stupaq.cloudatlas.messaging.messages.KnownZonesRequest;
import stupaq.cloudatlas.messaging.messages.KnownZonesResponse;
import stupaq.cloudatlas.naming.GlobalName;
import stupaq.cloudatlas.services.rmiserver.protocol.LocalClientProtocol;
import stupaq.cloudatlas.services.zonemanager.ZoneManagementInfo;
import stupaq.commons.util.concurrent.AsynchronousInvoker.DirectInvocation;
import stupaq.compact.SerializableWrapper;

import static stupaq.compact.SerializableWrapper.wrap;

public class LocalClientHandler implements LocalClientProtocol {
  private final MessageBus bus;

  public LocalClientHandler(MessageBus bus) {
    this.bus = bus;
    // We're ready to cooperate
    bus.register(new HandlerListener());
  }

  @Override
  public void updateAttributes(SerializableWrapper<AttributesUpdateMessage> message) {
    bus.post(message.get());
  }

  @Override
  public SerializableWrapper<ZoneManagementInfo> getAttributes(
      SerializableWrapper<GlobalName> globalName) throws RemoteException {
    DumpZoneRequest request = new DumpZoneRequest(globalName.get());
    bus.post(request);
    try {
      return wrap(Uninterruptibles.getUninterruptibly(request.context()).getZmi());
    } catch (ExecutionException e) {
      throw new RemoteException(e.getMessage());
    }
  }

  @Override
  public SerializableWrapper<EntitiesValuesResponse> getValues(
      SerializableWrapper<EntitiesValuesRequest> request) throws RemoteException {
    request.get().attach(SettableFuture.<EntitiesValuesResponse>create());
    bus.post(request.get());
    try {
      return wrap(Uninterruptibles.getUninterruptibly(request.get().context()));
    } catch (ExecutionException e) {
      throw new RemoteException(e.getMessage());
    }
  }

  @Override
  public void setFallbackContacts(SerializableWrapper<FallbackContactsMessage> message)
      throws RemoteException {
    bus.post(message.get());
  }

  @Override
  public SerializableWrapper<KnownZonesResponse> getKnownZones() throws RemoteException {
    KnownZonesRequest request = new KnownZonesRequest();
    bus.post(request);
    return wrap(awaitResult(request));
  }

  private <Result> Result awaitResult(Request<SettableFuture<Result>> request)
      throws RemoteException {
    try {
      return Uninterruptibles.getUninterruptibly(request.context());
    } catch (ExecutionException e) {
      throw new RemoteException(e.getMessage());
    }
  }

  private static interface HandlerContract {
    @Subscribe
    @DirectInvocation
    public void handleResponse(DumpZoneResponse response);

    @Subscribe
    @DirectInvocation
    public void handleResponse(KnownZonesResponse response);

    @Subscribe
    @DirectInvocation
    void handleResponse(EntitiesValuesResponse response);
  }

  private static class HandlerListener extends AbstractMessageListener implements HandlerContract {
    protected HandlerListener() {
      super(MoreExecutors.sameThreadExecutor(), HandlerContract.class);
    }

    @Override
    public void handleResponse(DumpZoneResponse response) {
      response.request().context().set(response);
    }

    @Override
    public void handleResponse(KnownZonesResponse response) {
      response.request().context().set(response);
    }

    @Override
    public void handleResponse(EntitiesValuesResponse response) {
      response.request().context().set(response);
    }
  }
}
