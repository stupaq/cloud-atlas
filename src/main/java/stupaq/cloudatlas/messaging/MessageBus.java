package stupaq.cloudatlas.messaging;

import com.google.common.eventbus.EventBus;
import com.google.common.reflect.Reflection;
import com.google.common.util.concurrent.ListeningExecutorService;

import stupaq.guava.util.concurrent.AsynchronousInvoker;

public final class MessageBus {
  private final EventBus internalBus = new EventBus();

  public void register(MessageListener listener, ListeningExecutorService executor) {
    MessageListener proxy =
        Reflection.newProxy(listener.getClass(), new AsynchronousInvoker(listener, executor));
    internalBus.register(proxy);
  }

  public void post(Message message) {
    internalBus.post(message);
  }
}
