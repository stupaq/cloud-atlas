package stupaq.commons.util.concurrent;

import com.google.common.util.concurrent.AbstractScheduledService.CustomScheduler;

import java.util.concurrent.TimeUnit;

public abstract class FastStartScheduler extends CustomScheduler {
  private final double ratio;
  private boolean firstShot = true;

  public FastStartScheduler() {
    this(0.05);
  }

  public FastStartScheduler(double ratio) {
    this.ratio = ratio;
  }

  @Override
  protected Schedule getNextSchedule() throws Exception {
    long delay = getNextDelayMs();
    if (firstShot) {
      delay *= ratio;
      firstShot = false;
    }
    return new Schedule(delay, TimeUnit.MILLISECONDS);
  }

  protected abstract long getNextDelayMs() throws Exception;
}
