package stupaq.cloudatlas.messaging;

import com.google.common.eventbus.EventBus;
import com.google.common.reflect.Reflection;

import stupaq.commons.util.concurrent.AsynchronousInvoker;

public final class MessageBus {
  private final EventBus internalBus = new EventBus();

  public void register(MessageListener listener) {
    Object proxy = Reflection
        .newProxy(listener.contract(), new AsynchronousInvoker(listener, listener.executor()));
    internalBus.register(proxy);
  }

  public void post(Message message) {
    internalBus.post(message);
  }
}
