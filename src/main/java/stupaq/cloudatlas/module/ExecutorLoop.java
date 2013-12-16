package stupaq.cloudatlas.module;

import com.google.common.util.concurrent.ForwardingListeningExecutorService;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.common.util.concurrent.ThreadFactoryBuilder;

import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class ExecutorLoop extends ForwardingListeningExecutorService
    implements ScheduledExecutorService {
  private final ScheduledExecutorService scheduled;
  private final ListeningExecutorService listening;

  public ExecutorLoop(ThreadFactoryBuilder builder) {
    // It is crucial for executor to use one and only thread during entire it's lifetime
    scheduled = Executors.newSingleThreadScheduledExecutor(builder.build());
    listening = MoreExecutors.listeningDecorator(scheduled);
  }

  @Override
  protected ListeningExecutorService delegate() {
    return listening;
  }

  @Override
  public ScheduledFuture<?> schedule(Runnable command, long delay, TimeUnit unit) {
    return scheduled.schedule(command, delay, unit);
  }

  @Override
  public <V> ScheduledFuture<V> schedule(Callable<V> callable, long delay, TimeUnit unit) {
    return schedule(callable, delay, unit);
  }

  @Override
  public ScheduledFuture<?> scheduleAtFixedRate(Runnable command, long initialDelay, long period,
      TimeUnit unit) {
    return scheduleAtFixedRate(command, initialDelay, period, unit);
  }

  @Override
  public ScheduledFuture<?> scheduleWithFixedDelay(Runnable command, long initialDelay, long delay,
      TimeUnit unit) {
    return scheduleWithFixedDelay(command, initialDelay, delay, unit);
  }
}
