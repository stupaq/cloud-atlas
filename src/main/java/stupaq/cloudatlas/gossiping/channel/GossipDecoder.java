package stupaq.cloudatlas.gossiping.channel;

import com.google.common.base.Preconditions;
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
import stupaq.cloudatlas.gossiping.GossipingInternalsHelpers;
import stupaq.cloudatlas.gossiping.dataformat.WireGossip;
import stupaq.cloudatlas.gossiping.peerstate.GossipIdDuplicate;
import stupaq.cloudatlas.messaging.messages.gossips.Gossip;
import stupaq.cloudatlas.messaging.messages.gossips.InboundGossip;
import stupaq.compact.CompactInput;
import stupaq.compact.TypeRegistry;

/** PACKAGE-LOCAL */
class GossipDecoder extends MessageToMessageDecoder<WireGossip> {
  private static final Log LOG = LogFactory.getLog(GossipDecoder.class);
  private final LoadingCache<CAContact, GossipIdDuplicate> duplicates;

  public GossipDecoder(final BootstrapConfiguration config) {
    Preconditions.checkState(!isSharable());
    duplicates = GossipingInternalsHelpers.contactsInfoCache(config)
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
    if (duplicates.getUnchecked(msg.contact()).apply(msg.gossipId())) {
      LOG.warn("Duplicated gossip: " + msg.gossipId() + " will be dropped");
      return;
    }
    LOG.trace("Decoding gossip: " + msg.gossipId());
    try (CompactInput stream = new CompactInput(msg.dataStream())) {
      Gossip gossip = TypeRegistry.readObject(stream);
      gossip.readsFrom(msg);
      out.add(new InboundGossip(gossip));
    }
  }

  @Override
  public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
    LOG.error("Decoder failed", cause);
  }
}
