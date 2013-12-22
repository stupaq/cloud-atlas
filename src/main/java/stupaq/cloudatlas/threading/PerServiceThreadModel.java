package stupaq.cloudatlas.threading;

import com.google.common.util.concurrent.Service;

import java.util.concurrent.ExecutorService;

import stupaq.commons.util.concurrent.SingleThreadedExecutor;

public class PerServiceThreadModel implements ThreadModel {
  public PerServiceThreadModel() {
  }

  @Override
  public SingleThreadedExecutor singleThreaded(Class<? extends Service> service) {
    return new SingleThreadedExecutor();
  }

  @Override
  public void free(ExecutorService executor) {
    executor.shutdown();
  }
}
