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
import stupaq.cloudatlas.messaging.messages.gossips.ZonesInterestGossip;
import stupaq.cloudatlas.messaging.messages.gossips.ZonesInterestInitialGossip;
import stupaq.cloudatlas.messaging.messages.gossips.ZonesUpdateGossip;
import stupaq.cloudatlas.time.LocalClock;

/** PACKAGE-LOCAL */
class GTPInboundHandler extends MessageToMessageDecoder<InboundGossip> {
  private static final Log LOG = LogFactory.getLog(GTPInboundHandler.class);
  private final LocalClock clock;

  public GTPInboundHandler(BootstrapConfiguration config) {
    clock = config.clock();
  }

  @Override
  protected void decode(ChannelHandlerContext ctx, InboundGossip msg, List<Object> out) {
    Gossip gossip = msg.gossip();
    try {
      if (gossip instanceof ZonesInterestInitialGossip) {
        adjust((ZonesInterestInitialGossip) gossip);
      } else if (gossip instanceof ZonesInterestGossip) {
        adjust((ZonesInterestGossip) gossip);
      } else if (gossip instanceof ZonesUpdateGossip) {
        adjust((ZonesUpdateGossip) gossip);
      } else {
        LOG.warn("Gossip: " + gossip.getClass() + " is not recognized by timestamp adjuster");
      }
    } catch (Throwable t) {
      LOG.error("Failed to adjust timestamps, forwarding message: " + gossip.getClass());
    }
    // Forward the same message
    ReferenceCountUtil.retain(msg);
    out.add(msg);
  }

  private void adjust(ZonesInterestInitialGossip gossip) {
  }

  private void adjust(ZonesInterestGossip gossip) {
  }

  private void adjust(ZonesUpdateGossip gossip) {
  }

  @Override
  public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
    LOG.error("Handler failed", cause);
  }
}
