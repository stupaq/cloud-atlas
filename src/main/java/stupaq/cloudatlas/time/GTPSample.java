package stupaq.cloudatlas.time;

import com.google.common.base.Preconditions;

import javax.annotation.concurrent.Immutable;

@Immutable
public class GTPSample {
  public final long roundTrip;
  /** Definition of this field influences {@link stupaq.cloudatlas.time.GTPOffset} implementation */
  public final long difference;

  public GTPSample(long[] samples) {
    Preconditions.checkArgument(samples.length == 4);
    roundTrip = (samples[3] - samples[0]) - (samples[2] - samples[1]);
    difference = Math.round(samples[2] + 0.5 * roundTrip - samples[3]);
  }

  @Override
  public String toString() {
    return "GTPSample{roundTrip=" + roundTrip + ", difference=" + difference + '}';
  }
}
