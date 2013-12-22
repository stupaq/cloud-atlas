package stupaq.cloudatlas.threading;

import com.google.common.util.concurrent.Service;

import java.util.concurrent.ExecutorService;

import stupaq.commons.util.concurrent.SingleThreadedExecutor;

public class SingleThreadModel implements ThreadModel {
  private SingleThreadedExecutor executor = null;
  private int refCount = 0;

  @Override
  public synchronized SingleThreadedExecutor singleThreaded(Class<? extends Service> service) {
    if (executor == null) {
      executor = new SingleThreadedExecutor();
    }
    refCount++;
    return executor;
  }

  @Override
  public synchronized void free(ExecutorService executor) {
    refCount--;
    if (refCount == 0) {
      executor.shutdown();
    }
  }
}
