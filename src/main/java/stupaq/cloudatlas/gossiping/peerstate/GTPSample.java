package stupaq.cloudatlas.gossiping.peerstate;

import com.google.common.base.Preconditions;

import javax.annotation.concurrent.Immutable;

@Immutable
public class GTPSample {
  public final long roundTrip;
  public final long difference;

  public GTPSample(long[] samples) {
    Preconditions.checkArgument(samples.length == 4);
    roundTrip = (samples[3] - samples[0]) - (samples[2] - samples[1]);
    difference = Math.round(samples[2] + 0.5 * roundTrip - samples[3]);
  }
}
