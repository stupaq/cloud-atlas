package stupaq.cloudatlas.gossiping.channel;

import com.google.common.cache.CacheLoader;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.IOException;
import java.util.List;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageCodec;
import io.netty.util.ReferenceCountUtil;
import stupaq.cloudatlas.attribute.values.CAContact;
import stupaq.cloudatlas.configuration.BootstrapConfiguration;
import stupaq.cloudatlas.gossiping.GossipingConfigKeys;
import stupaq.cloudatlas.gossiping.dataformat.WireGossip;
import stupaq.cloudatlas.gossiping.peerstate.ContactStateCache;
import stupaq.cloudatlas.gossiping.peerstate.GossipIdAllocator;
import stupaq.cloudatlas.messaging.messages.gossips.Gossip;
import stupaq.cloudatlas.messaging.messages.gossips.OutboundGossip;
import stupaq.compact.CompactInput;
import stupaq.compact.CompactOutput;
import stupaq.compact.TypeRegistry;

/** PACKAGE-LOCAL */
class GossipCodec extends MessageToMessageCodec<WireGossip, OutboundGossip>
    implements GossipingConfigKeys {
  private static final Log LOG = LogFactory.getLog(GossipCodec.class);
  private final ContactStateCache<GossipIdAllocator> contacts;

  public GossipCodec(BootstrapConfiguration config) {
    contacts = new ContactStateCache<>(config, new CacheLoader<CAContact, GossipIdAllocator>() {
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
      out.add(new WireGossip(msg.recipient(), contacts.get(msg.recipient()).next(), buffer));
    } catch (Throwable t) {
      LOG.error("Encoding failed", t);
      // Ignore as we do not close the only channel
    } finally {
      ReferenceCountUtil.release(buffer);
    }
  }

  @Override
  protected void decode(ChannelHandlerContext ctx, WireGossip msg, List<Object> out)
      throws IOException {
    try {
      CompactInput stream = new CompactInput(msg.dataStream());
      Gossip gossip = TypeRegistry.readObject(stream);
      if (!gossip.hasSender()) {
        gossip.sender(msg.contact());
      }
      out.add(gossip);
    } catch (Throwable t) {
      LOG.error("Decoding failed", t);
      // Ignore as we do not close the only channel
    }
  }

}
