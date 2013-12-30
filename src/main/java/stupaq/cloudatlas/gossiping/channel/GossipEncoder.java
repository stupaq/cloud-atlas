package stupaq.cloudatlas.gossiping.channel;

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
import stupaq.cloudatlas.configuration.BootstrapConfiguration;
import stupaq.cloudatlas.gossiping.GossipingInternalsConfigKeys;
import stupaq.cloudatlas.gossiping.dataformat.WireGossip;
import stupaq.cloudatlas.messaging.messages.gossips.OutboundGossip;
import stupaq.compact.CompactOutput;
import stupaq.compact.TypeRegistry;

/** PACKAGE-LOCAL */
class GossipEncoder extends MessageToMessageEncoder<OutboundGossip>
    implements GossipingInternalsConfigKeys {
  private static final Log LOG = LogFactory.getLog(GossipEncoder.class);

  public GossipEncoder(BootstrapConfiguration config) {
  }

  @Override
  protected void encode(ChannelHandlerContext ctx, OutboundGossip msg, List<Object> out)
      throws IOException, ExecutionException {
    ByteBuf buffer = Unpooled.buffer();
    try (CompactOutput stream = new CompactOutput(new ByteBufOutputStream(buffer))) {
      TypeRegistry.writeObject(stream, msg.gossip());
      out.add(new WireGossip(msg.recipient(), msg.gossip().id(), buffer));
    } finally {
      ReferenceCountUtil.release(buffer);
    }
  }

  @SuppressWarnings("deprecation")
  @Override
  public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
    LOG.error("Decoding failed", cause);
  }
}
