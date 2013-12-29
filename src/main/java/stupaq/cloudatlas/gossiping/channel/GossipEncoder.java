package stupaq.cloudatlas.gossiping.channel;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.IOException;
import java.util.List;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;
import io.netty.util.ReferenceCountUtil;
import stupaq.cloudatlas.attribute.values.CAContact;
import stupaq.cloudatlas.configuration.BootstrapConfiguration;
import stupaq.cloudatlas.gossiping.GossipingConfigKeys;
import stupaq.cloudatlas.gossiping.dataformat.WireGossip;
import stupaq.cloudatlas.gossiping.peerstate.GossipIdAllocator;
import stupaq.cloudatlas.messaging.messages.gossips.OutboundGossip;
import stupaq.compact.CompactOutput;
import stupaq.compact.TypeRegistry;

/** PACKAGE-LOCAL */
class GossipEncoder extends MessageToMessageEncoder<OutboundGossip> implements GossipingConfigKeys {
  private static final Log LOG = LogFactory.getLog(GossipEncoder.class);
  private final LoadingCache<CAContact, GossipIdAllocator> allocators;

  public GossipEncoder(BootstrapConfiguration config) {
    int maxSize = config.getInt(EXPECTED_CONTACTS_MAX_COUNT, EXPECTED_CONTACTS_MAX_COUNT_DEFAULT);
    allocators = CacheBuilder.newBuilder()
        /** This is deliberate as {@link GossipDecoder} will set maximum size to half of that. */
        .maximumSize(maxSize * 2)
        .build(new CacheLoader<CAContact, GossipIdAllocator>() {
          @Override
          public GossipIdAllocator load(CAContact key) throws Exception {
            return new GossipIdAllocator();
          }
        });
  }

  @Override
  protected void encode(ChannelHandlerContext ctx, OutboundGossip msg, List<Object> out)
      throws IOException {
    ByteBuf buffer = null;
    try {
      buffer = Unpooled.buffer();
      CompactOutput stream = new CompactOutput(new ByteBufOutputStream(buffer));
      TypeRegistry.writeObject(stream, msg.gossip());
      out.add(new WireGossip(msg.recipient(), allocators.get(msg.recipient()).next(), buffer));
    } catch (Throwable t) {
      LOG.error("Encoding failed", t);
      // Ignore as we do not close the only channel
    } finally {
      ReferenceCountUtil.release(buffer);
    }
  }
}