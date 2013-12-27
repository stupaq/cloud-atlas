package stupaq.cloudatlas.gossiping.channel;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.List;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.DatagramPacket;
import io.netty.handler.codec.MessageToMessageCodec;
import stupaq.cloudatlas.gossiping.dataformat.Frame;

/** PACKAGE-LOCAL */
class DatagramCodec extends MessageToMessageCodec<DatagramPacket, Frame> {
  private static final Log LOG = LogFactory.getLog(DatagramCodec.class);

  @Override
  protected void encode(ChannelHandlerContext ctx, Frame msg, List<Object> out) {
    try {
      // As per convention the packet reference count has been incremented
      out.add(msg.packet());
    } catch (Throwable t) {
      LOG.error("Encoding failed", t);
      // Ignore as we do not close the only channel
    }
  }

  @Override
  protected void decode(ChannelHandlerContext ctx, DatagramPacket msg, List<Object> out) {
    try {
      // As per convention the packet reference will be incremented in the constructor if needed
      out.add(new Frame(msg));
    } catch (Throwable t) {
      LOG.error("Decoding failed", t);
      // Ignore as we do not close the only channel
    }
  }
}
