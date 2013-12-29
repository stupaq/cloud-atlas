package stupaq.cloudatlas.gossiping.channel;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.IOException;
import java.util.List;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.DatagramPacket;
import io.netty.handler.codec.MessageToMessageDecoder;
import stupaq.cloudatlas.configuration.BootstrapConfiguration;
import stupaq.cloudatlas.gossiping.dataformat.WireFrame;

/** PACKAGE-LOCAL */
class DatagramDecoder extends MessageToMessageDecoder<DatagramPacket> {
  private static final Log LOG = LogFactory.getLog(DatagramDecoder.class);

  public DatagramDecoder(BootstrapConfiguration config) {
  }

  @Override
  protected void decode(ChannelHandlerContext ctx, DatagramPacket msg, List<Object> out)
      throws IOException {
    // As per convention the packet reference will be incremented in the constructor if needed
    out.add(new WireFrame(msg));
  }

  @Override
  public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
    LOG.error("Decoding failed", cause);
  }
}
