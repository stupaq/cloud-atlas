package stupaq.cloudatlas.threading;

import com.google.common.util.concurrent.Service;

import java.util.concurrent.ExecutorService;

import stupaq.commons.util.concurrent.SingleThreadedExecutor;

public interface ThreadModel {
  SingleThreadedExecutor singleThreaded(Class<? extends Service> service);

  void free(ExecutorService executor);
}
