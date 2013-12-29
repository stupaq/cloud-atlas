package stupaq.cloudatlas.gossiping.channel;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.List;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;
import stupaq.cloudatlas.configuration.BootstrapConfiguration;
import stupaq.cloudatlas.gossiping.dataformat.WireFrame;

/** PACKAGE-LOCAL */
class DatagramEncoder extends MessageToMessageEncoder<WireFrame> {
  private static final Log LOG = LogFactory.getLog(DatagramEncoder.class);

  public DatagramEncoder(BootstrapConfiguration config) {
  }

  @Override
  protected void encode(ChannelHandlerContext ctx, WireFrame msg, List<Object> out) {
    // As per convention the packet reference count has been incremented
    out.add(msg.packet());
  }

  @Override
  public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
    LOG.error("Encoding failed", cause);
  }
}
