package stupaq.cloudatlas.time;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;

import stupaq.cloudatlas.attribute.values.CAContact;
import stupaq.cloudatlas.configuration.CAConfiguration;
import stupaq.cloudatlas.gossiping.GossipingConfigKeys;

public class GTPSynchronizedClock extends LocalClock implements GossipingConfigKeys {
  private static final Log LOG = LogFactory.getLog(GTPSynchronizedClock.class);
  private final LoadingCache<CAContact, GTPOffsetInformation> offsets;

  public GTPSynchronizedClock(CAConfiguration config) {
    offsets = CacheBuilder.newBuilder()
        .expireAfterAccess(config.getLong(GTP_OFFSET_RETENTION, GTP_OFFSET_RETENTION_DEFAULT),
            TimeUnit.MILLISECONDS)
        .build(new CacheLoader<CAContact, GTPOffsetInformation>() {
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

  public GTPOffset offset(CAContact contact) {
    GTPOffset offset = offsets.getUnchecked(contact).offset();
    if (LOG.isInfoEnabled()) {
      LOG.info("Responded with offset: " + offset + " associated with: " + contact);
    }
    return offset;
  }
}
