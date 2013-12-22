package stupaq.cloudatlas.threading;

import com.google.common.util.concurrent.Service;

import java.util.concurrent.ExecutorService;

import stupaq.commons.util.concurrent.SingleThreadedExecutor;

public class SingleThreadModel implements ThreadModel {
  private final SingleThreadedExecutor executor;

  public SingleThreadModel() {
    executor = new SingleThreadedExecutor();
  }

  @Override
  public SingleThreadedExecutor singleThreaded(Class<? extends Service> service) {
    return executor;
  }

  @Override
  public void free(ExecutorService executor) {
    // Do nothing
  }
}
