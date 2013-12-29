package stupaq.cloudatlas.time;

import java.util.concurrent.TimeUnit;

public class LocalClock {
  public long timestamp() {
    return System.currentTimeMillis();
  }

  public long timestamp(long advance, TimeUnit unit) {
    return timestamp() + unit.toMillis(advance);
  }
}
