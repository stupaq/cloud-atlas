package stupaq.cloudatlas.gossiping.peerstate;

import com.google.common.base.Preconditions;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import java.util.concurrent.TimeUnit;

import stupaq.cloudatlas.configuration.BootstrapConfiguration;
import stupaq.cloudatlas.gossiping.GossipingConfigKeys;
import stupaq.cloudatlas.gossiping.dataformat.GossipId;
import stupaq.cloudatlas.gossiping.dataformat.WireFrame;
import stupaq.commons.cache.CacheSet;
import stupaq.commons.cache.ReferenceCountedRemovalListener;

public class ContactInfo implements GossipingConfigKeys {
  private final LoadingCache<GossipId, GossipInfo> received;
  private final CacheSet<GossipId> heardAbout;

  public ContactInfo(BootstrapConfiguration config) {
    received = CacheBuilder.newBuilder()
        .expireAfterAccess(
            config.getLong(FRAME_ASSEMBLING_TIMEOUT, FRAME_ASSEMBLING_TIMEOUT_DEFAULT),
            TimeUnit.MILLISECONDS)
        .removalListener(new ReferenceCountedRemovalListener())
        .build(new CacheLoader<GossipId, GossipInfo>() {
          @Override
          public GossipInfo load(GossipId key) {
            Preconditions.checkArgument(!heardAbout.contains(key));
            return new GossipInfo();
          }
        });
    // This has longer retention but smaller memory footprint, this way we can store information
    // about all recently received messages and prevent duplicates from popping out here and there.
    // We'd like to have a BloomFilter here... we I can't.
    heardAbout = new CacheSet<>(CacheBuilder.newBuilder()
        .expireAfterAccess(config.getLong(GOSSIP_ID_RETENTION, GOSSIP_ID_RETENTION_DEFAULT),
            TimeUnit.MILLISECONDS));
  }

  public GossipInfo add(WireFrame msg) throws Exception {
    GossipId id = msg.frameId().gossipId();
    GossipInfo info = received.get(id);
    if (info.add(msg)) {
      info.retain();
      // This calls release automatically
      received.invalidate(id);
      return info;
    }
    return null;
  }
}
