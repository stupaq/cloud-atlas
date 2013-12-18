package stupaq.cloudatlas.messaging;

import com.google.common.base.Preconditions;
import com.google.common.util.concurrent.ListeningExecutorService;

public interface MessageListener {
  Class<?> contract();

  public ListeningExecutorService executor();

  public static abstract class AbstractMessageListener implements MessageListener {
    private final ListeningExecutorService executor;
    private final Class<?> contract;

    protected AbstractMessageListener(ListeningExecutorService executor, Class<?> contract) {
      Preconditions.checkArgument(contract.isAssignableFrom(this.getClass()));
      this.executor = executor;
      this.contract = contract;
    }

    @Override
    public Class<?> contract() {
      return contract;
    }

    @Override
    public ListeningExecutorService executor() {
      return executor;
    }
  }
}
