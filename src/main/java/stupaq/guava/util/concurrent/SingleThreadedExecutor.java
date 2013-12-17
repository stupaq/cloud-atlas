package stupaq.guava.util.concurrent;

import com.google.common.util.concurrent.ForwardingListeningExecutorService;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.common.util.concurrent.ThreadFactoryBuilder;

import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import javax.annotation.Nonnull;

public class SingleThreadedExecutor extends ForwardingListeningExecutorService
    implements ScheduledExecutorService {
  private final ScheduledExecutorService scheduled;
  private final ListeningExecutorService listening;

  public SingleThreadedExecutor(ThreadFactoryBuilder builder) {
    // It is crucial for executor to use one and only thread during entire it's lifetime
    scheduled = Executors.newSingleThreadScheduledExecutor(builder.build());
    listening = MoreExecutors.listeningDecorator(scheduled);
  }

  @Override
  protected ListeningExecutorService delegate() {
    return listening;
  }

  @Nonnull
  @Override
  public ScheduledFuture<?> schedule(@Nonnull Runnable command, long delay,
      @Nonnull TimeUnit unit) {
    return scheduled.schedule(command, delay, unit);
  }

  @Nonnull
  @Override
  public <V> ScheduledFuture<V> schedule(@Nonnull Callable<V> callable, long delay,
      @Nonnull TimeUnit unit) {
    return scheduled.schedule(callable, delay, unit);
  }

  @Nonnull
  @Override
  public ScheduledFuture<?> scheduleAtFixedRate(@Nonnull Runnable command, long initialDelay,
      long period, @Nonnull TimeUnit unit) {
    return scheduled.scheduleAtFixedRate(command, initialDelay, period, unit);
  }

  @Nonnull
  @Override
  public ScheduledFuture<?> scheduleWithFixedDelay(@Nonnull Runnable command, long initialDelay,
      long delay, @Nonnull TimeUnit unit) {
    return scheduled.scheduleWithFixedDelay(command, initialDelay, delay, unit);
  }
}
