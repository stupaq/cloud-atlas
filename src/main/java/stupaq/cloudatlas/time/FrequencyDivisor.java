package stupaq.cloudatlas.time;

import com.google.common.base.Preconditions;

public class FrequencyDivisor {
  private int ratio;
  private int counter;

  public FrequencyDivisor(long desired, long tick) {
    this(desired, tick, Bias.NATURAL);
  }

  public FrequencyDivisor(long desired, long tick, Bias bias) {
    Preconditions.checkArgument(desired >= tick);
    counter = ratio = (int) getRatio(desired, tick, bias);
  }

  public boolean tick() {
    if (--counter <= 0) {
      counter = ratio;
      return true;
    }
    return false;
  }

  private double getRatio(long desired, long tick, Bias bias) {
    switch (bias) {
      case SHORTER:
        return Math.floor(desired / tick);
      case LONGER:
        return Math.ceil(desired / tick);
      case NATURAL:
        return Math.round(desired / tick);
    }
    assert false;
    return 1;
  }

  public void reset(long desired, long tick) {
    reset(desired, tick, Bias.NATURAL);
  }

  public void reset(long desired, long tick, Bias bias) {
    ratio = (int) getRatio(desired, tick, bias);
  }

  public static enum Bias {
    SHORTER,
    LONGER,
    NATURAL
  }
}
