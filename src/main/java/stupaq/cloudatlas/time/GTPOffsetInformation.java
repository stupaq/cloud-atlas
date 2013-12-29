package stupaq.cloudatlas.time;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import stupaq.commons.math.OnlineMeanCalculator;

/** PACKAGE-LOCAL */
class GTPOffsetInformation {
  private static final Log LOG = LogFactory.getLog(GTPOffsetInformation.class);
  private final OnlineMeanCalculator difference = new OnlineMeanCalculator();
  private final OnlineMeanCalculator roundTrip = new OnlineMeanCalculator();

  public void update(GTPSample sample) {
    roundTrip.add(sample.roundTrip);
    // I'm truly sorry but deep in my heart I'm a physicist
    if (roundTrip.mean() + 3 * roundTrip.sigma() > sample.roundTrip) {
      difference.add(sample.difference);
    } else if (LOG.isInfoEnabled()) {
      LOG.info("Discarded " + sample + " - huge round trip time (accumulated: " + roundTrip + ')');
    }
  }

  public GTPOffset offset() {
    return new GTPOffset(Math.round(difference.mean()));
  }
}
