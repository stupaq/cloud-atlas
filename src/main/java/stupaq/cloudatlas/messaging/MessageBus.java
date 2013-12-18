package stupaq.cloudatlas.messaging;

import com.google.common.eventbus.DeadEvent;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.common.reflect.Reflection;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import stupaq.commons.util.concurrent.AsynchronousInvoker;

public final class MessageBus {
  private static final Log LOG = LogFactory.getLog(MessageBus.class);
  private final EventBus internalBus = new EventBus();

  public MessageBus() {
    internalBus.register(new DeadEventHandler());
  }

  public void register(MessageListener listener) {
    Object proxy = Reflection
        .newProxy(listener.contract(), new AsynchronousInvoker(listener, listener.executor()));
    internalBus.register(proxy);
  }

  public void post(Message message) {
    internalBus.post(message);
  }

  private class DeadEventHandler {
    @Subscribe
    public void handle(DeadEvent event) {
      LOG.info("Message: " + event.getEvent().getClass().getSimpleName() + " could not be routed.");
    }
  }
}
