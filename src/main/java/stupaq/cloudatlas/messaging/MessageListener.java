package stupaq.cloudatlas.messaging;

import com.google.common.util.concurrent.ListeningExecutorService;

public interface MessageListener {
  Class<? extends MessageListener> contract();

  public ListeningExecutorService executor();
}
