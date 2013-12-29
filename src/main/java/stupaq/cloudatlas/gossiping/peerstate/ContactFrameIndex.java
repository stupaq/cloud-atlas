package stupaq.cloudatlas.gossiping.peerstate;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import java.util.concurrent.TimeUnit;

import stupaq.cloudatlas.configuration.BootstrapConfiguration;
import stupaq.cloudatlas.gossiping.GossipingInternalsConfigKeys;
import stupaq.cloudatlas.gossiping.dataformat.GossipId;
import stupaq.cloudatlas.gossiping.dataformat.WireFrame;
import stupaq.commons.cache.ReferenceCountedRemovalListener;

public class ContactFrameIndex implements GossipingInternalsConfigKeys {
  private final LoadingCache<GossipId, GossipFrameIndex> received;

  public ContactFrameIndex(BootstrapConfiguration config) {
    received = CacheBuilder.newBuilder()
        .expireAfterAccess(
            config.getLong(FRAME_ASSEMBLING_TIMEOUT, FRAME_ASSEMBLING_TIMEOUT_DEFAULT),
            TimeUnit.MILLISECONDS)
        .removalListener(new ReferenceCountedRemovalListener())
        .build(new CacheLoader<GossipId, GossipFrameIndex>() {
          @Override
          public GossipFrameIndex load(GossipId key) {
            return new GossipFrameIndex();
          }
        });
  }

  public GossipFrameIndex add(WireFrame msg) throws Exception {
    GossipId id = msg.frameId().gossipId();
    GossipFrameIndex info = received.get(id);
    if (info.add(msg)) {
      info.retain();
      // This calls release automatically
      received.invalidate(id);
      return info;
    }
    return null;
  }
}
