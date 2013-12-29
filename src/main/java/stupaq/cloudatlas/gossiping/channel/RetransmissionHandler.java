package stupaq.cloudatlas.gossiping.channel;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.List;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageCodec;
import io.netty.util.ReferenceCountUtil;
import stupaq.cloudatlas.configuration.BootstrapConfiguration;
import stupaq.cloudatlas.gossiping.dataformat.WireGossip;

/** PACKAGE-LOCAL */
class RetransmissionHandler extends MessageToMessageCodec<WireGossip, WireGossip> {
  private static final Log LOG = LogFactory.getLog(RetransmissionHandler.class);

  public RetransmissionHandler(BootstrapConfiguration config) {
  }

  @Override
  protected void encode(ChannelHandlerContext ctx, WireGossip msg, List<Object> out)
      throws Exception {
    ReferenceCountUtil.retain(msg);
    out.add(msg);
  }

  @Override
  protected void decode(ChannelHandlerContext ctx, WireGossip msg, List<Object> out)
      throws Exception {
    ReferenceCountUtil.retain(msg);
    out.add(msg);
  }

  @Override
  public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
    LOG.error("Handler failed", cause);
  }
}
