package stupaq.cloudatlas.messaging;

import com.google.common.util.concurrent.ListeningExecutorService;

public interface MessageListener {
  Class<?> getInterface();

  public ListeningExecutorService executor();
}
