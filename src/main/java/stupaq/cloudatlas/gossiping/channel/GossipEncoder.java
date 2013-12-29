package stupaq.cloudatlas.gossiping.channel;

import com.google.common.base.Preconditions;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;
import io.netty.util.ReferenceCountUtil;
import stupaq.cloudatlas.attribute.values.CAContact;
import stupaq.cloudatlas.configuration.BootstrapConfiguration;
import stupaq.cloudatlas.gossiping.GossipingInternalsConfigKeys;
import stupaq.cloudatlas.gossiping.dataformat.WireGossip;
import stupaq.cloudatlas.gossiping.peerstate.GossipIdAllocator;
import stupaq.cloudatlas.messaging.messages.gossips.OutboundGossip;
import stupaq.compact.CompactOutput;
import stupaq.compact.TypeRegistry;

/** PACKAGE-LOCAL */
class GossipEncoder extends MessageToMessageEncoder<OutboundGossip> implements
                                                                    GossipingInternalsConfigKeys {
  private static final Log LOG = LogFactory.getLog(GossipEncoder.class);
  private final LoadingCache<CAContact, GossipIdAllocator> allocators;

  public GossipEncoder(BootstrapConfiguration config) {
    Preconditions.checkState(!isSharable());
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
      throws IOException, ExecutionException {
    ByteBuf buffer = Unpooled.buffer();
    try (CompactOutput stream = new CompactOutput(new ByteBufOutputStream(buffer))) {
      TypeRegistry.writeObject(stream, msg.gossip());
      out.add(new WireGossip(msg.recipient(), allocators.get(msg.recipient()).next(), buffer));
    } finally {
      ReferenceCountUtil.release(buffer);
    }
  }

  @Override
  public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
    LOG.error("Decoding failed", cause);
  }
}
