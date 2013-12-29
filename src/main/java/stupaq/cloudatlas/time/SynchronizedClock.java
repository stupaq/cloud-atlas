package stupaq.cloudatlas.time;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;

import stupaq.cloudatlas.attribute.values.CAContact;
import stupaq.cloudatlas.gossiping.peerstate.GTPSample;

public class SynchronizedClock {
  private static final Log LOG = LogFactory.getLog(SynchronizedClock.class);

  public long timestamp() {
    return System.currentTimeMillis();
  }

  public long timestamp(long advance, TimeUnit unit) {
    return timestamp() + unit.toMillis(advance);
  }

  public void record(InetSocketAddress othersAddress, GTPSample sample) {
    record(new CAContact(othersAddress), sample);
  }

  private void record(CAContact contact, GTPSample sample) {
    LOG.info("Recorded from: " + contact + " a sample: " + sample);
    // FIXME
  }
}
