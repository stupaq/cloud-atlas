package stupaq.cloudatlas.gossiping.sessions;

import com.google.common.base.Preconditions;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import java.util.concurrent.TimeUnit;

import stupaq.cloudatlas.attribute.values.CAContact;
import stupaq.cloudatlas.configuration.BootstrapConfiguration;
import stupaq.cloudatlas.gossiping.GossipingConfigKeys;
import stupaq.cloudatlas.gossiping.dataformat.Frame;
import stupaq.cloudatlas.gossiping.dataformat.Frame.FramesBuilder;
import stupaq.cloudatlas.gossiping.dataformat.GossipId;
import stupaq.cloudatlas.time.LocalClock;
import stupaq.commons.cache.CacheSet;
import stupaq.commons.cache.ReferenceCountedRemovalListener;

public class ContactInfo implements GossipingConfigKeys {
  private final LoadingCache<GossipId, GossipInfo> received;
  private final CacheSet<GossipId> heardAbout;
  private final LocalClock clock;
  private int nextGossipId = 0;

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
    clock = config.clock();
  }

  public FramesBuilder nextGossip(int framesCount, CAContact destination) {
    GossipId gossipId = new GossipId(nextGossipId++, framesCount);
    return new FramesBuilder(clock, gossipId, destination);
  }

  public GossipInfo add(Frame msg) throws Exception {
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
