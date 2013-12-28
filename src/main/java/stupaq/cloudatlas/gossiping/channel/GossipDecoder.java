package stupaq.cloudatlas.gossiping.channel;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.IOException;
import java.util.List;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import stupaq.cloudatlas.attribute.values.CAContact;
import stupaq.cloudatlas.configuration.BootstrapConfiguration;
import stupaq.cloudatlas.gossiping.GossipingConfigKeys;
import stupaq.cloudatlas.gossiping.dataformat.WireGossip;
import stupaq.cloudatlas.gossiping.peerstate.GossipIdDuplicate;
import stupaq.cloudatlas.messaging.messages.gossips.Gossip;
import stupaq.cloudatlas.messaging.messages.gossips.InboundGossip;
import stupaq.compact.CompactInput;
import stupaq.compact.TypeRegistry;

/** PACKAGE-LOCAL */
class GossipDecoder extends MessageToMessageDecoder<WireGossip> implements GossipingConfigKeys {
  private static final Log LOG = LogFactory.getLog(GossipDecoder.class);
  private final LoadingCache<CAContact, GossipIdDuplicate> duplicates;

  public GossipDecoder(final BootstrapConfiguration config) {
    int maxSize = config.getInt(EXPECTED_CONTACTS_MAX_COUNT, EXPECTED_CONTACTS_MAX_COUNT_DEFAULT);
    duplicates = CacheBuilder.newBuilder()
        /** This is deliberate as {@link GossipEncoder} will set maximum size to twice that. */
        .maximumSize(maxSize)
        .build(new CacheLoader<CAContact, GossipIdDuplicate>() {
          @Override
          public GossipIdDuplicate load(CAContact key) throws Exception {
            return new GossipIdDuplicate(config);
          }
        });
  }

  @Override
  protected void decode(ChannelHandlerContext ctx, WireGossip msg, List<Object> out)
      throws IOException {
    try {
      if (duplicates.get(msg.contact()).apply(msg.gossipId())) {
        LOG.warn("Duplicate gossip: " + msg.gossipId() + " will be dropped");
        return;
      }
      CompactInput stream = new CompactInput(msg.dataStream());
      Gossip gossip = TypeRegistry.readObject(stream);
      if (!gossip.hasSender()) {
        gossip.sender(msg.contact());
      }
      out.add(new InboundGossip(gossip));
    } catch (Throwable t) {
      LOG.error("Decoding failed", t);
      // Ignore as we do not close the only channel
    }
  }
}
