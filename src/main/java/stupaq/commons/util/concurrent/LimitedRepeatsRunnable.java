package stupaq.commons.util.concurrent;

import com.google.common.base.Preconditions;
import com.google.common.util.concurrent.SettableFuture;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class LimitedRepeatsRunnable implements Runnable {
  private final Runnable runnable;
  private final AtomicInteger repeats;
  private final SettableFuture<ScheduledFuture<?>> future = SettableFuture.create();

  protected LimitedRepeatsRunnable(Runnable runnable, int limit) {
    Preconditions.checkArgument(limit > 0);
    this.runnable = runnable;
    repeats = new AtomicInteger(limit);
  }

  @Override
  public void run() {
    int repeat = repeats.decrementAndGet();
    if (repeat == 0) {
      try {
        future.get().cancel(false);
      } catch (InterruptedException | ExecutionException ignored) {
      }
    }
    if (repeat >= 0) {
      runnable.run();
    }
  }

  public static ScheduledFuture<?> scheduleAtFixedRate(ScheduledExecutorService executor,
      Runnable runnable, int limit, long initialDelay, long period, TimeUnit unit) {
    LimitedRepeatsRunnable wrapper = new LimitedRepeatsRunnable(runnable, limit);
    ScheduledFuture<?> future = executor.scheduleAtFixedRate(wrapper, initialDelay, period, unit);
    wrapper.future.set(future);
    return future;
  }
}
