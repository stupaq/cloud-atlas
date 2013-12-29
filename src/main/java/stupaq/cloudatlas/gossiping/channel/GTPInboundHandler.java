package stupaq.cloudatlas.gossiping.channel;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.List;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.netty.util.ReferenceCountUtil;
import stupaq.cloudatlas.configuration.BootstrapConfiguration;
import stupaq.cloudatlas.messaging.messages.gossips.Gossip;
import stupaq.cloudatlas.messaging.messages.gossips.InboundGossip;
import stupaq.cloudatlas.time.GTPSynchronizedClock;

/** PACKAGE-LOCAL */
class GTPInboundHandler extends MessageToMessageDecoder<InboundGossip> {
  private static final Log LOG = LogFactory.getLog(GTPInboundHandler.class);
  private final GTPSynchronizedClock clock;

  public GTPInboundHandler(BootstrapConfiguration config) {
    clock = config.synchronizedClock();
  }

  @Override
  protected void decode(ChannelHandlerContext ctx, InboundGossip msg, List<Object> out) {
    Gossip gossip = msg.gossip();
    try {
      gossip.adjustToLocal(clock.offset(msg.gossip().sender()));
    } catch (Throwable t) {
      LOG.error("Failed to adjust timestamps, forwarding message: " + gossip.getClass());
    }
    // Forward the same message
    ReferenceCountUtil.retain(msg);
    out.add(msg);
  }

  @Override
  public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
    LOG.error("Handler failed", cause);
  }
}
