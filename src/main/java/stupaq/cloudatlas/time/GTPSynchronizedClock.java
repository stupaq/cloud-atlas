package stupaq.cloudatlas.time;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.net.InetSocketAddress;

import stupaq.cloudatlas.attribute.values.CAContact;
import stupaq.cloudatlas.configuration.CAConfiguration;

public class GTPSynchronizedClock extends LocalClock {
  private static final Log LOG = LogFactory.getLog(GTPSynchronizedClock.class);
  private final LoadingCache<CAContact, GTPOffsetInformation> offsets;

  public GTPSynchronizedClock(CAConfiguration config) {
    offsets = CacheBuilder.newBuilder().build(new CacheLoader<CAContact, GTPOffsetInformation>() {
      @Override
      public GTPOffsetInformation load(CAContact key) throws Exception {
        return new GTPOffsetInformation();
      }
    });
  }

  public void record(InetSocketAddress othersAddress, GTPSample sample) {
    record(new CAContact(othersAddress), sample);
  }

  public void record(CAContact contact, GTPSample sample) {
    if (LOG.isInfoEnabled()) {
      LOG.info("Recorded sample: " + sample + " from: " + contact);
    }
    offsets.getUnchecked(contact).update(sample);
  }

  public long convertToLocal(CAContact contact, long timestamp) {
    return GTPSample.convertToLocal(timestamp, offsets.getUnchecked(contact).difference());
  }

  public long convertToRemote(CAContact contact, long timestamp) {
    return GTPSample.convertToRemote(timestamp, offsets.getUnchecked(contact).difference());
  }

  public LocalClock localClock() {
    return this;
  }
}
