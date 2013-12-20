package stupaq.cloudatlas.threading;

import com.google.common.util.concurrent.Service;

import java.util.concurrent.ExecutorService;

import stupaq.cloudatlas.configuration.CAConfiguration;
import stupaq.commons.util.concurrent.SingleThreadedExecutor;

public class ThreadManager {
  public ThreadManager(CAConfiguration config) {
  }

  public SingleThreadedExecutor singleThreaded(Class<? extends Service> service) {
    return new SingleThreadedExecutor();
  }

  public void free(ExecutorService executor) {
    executor.shutdown();
  }
}
