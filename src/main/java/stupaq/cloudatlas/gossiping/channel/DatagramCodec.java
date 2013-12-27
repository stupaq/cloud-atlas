package stupaq.cloudatlas.gossiping.channel;

import java.io.IOException;
import java.util.List;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.DatagramPacket;
import io.netty.handler.codec.MessageToMessageCodec;
import stupaq.cloudatlas.gossiping.dataformat.Frame;

/** PACKAGE-LOCAL */
class DatagramCodec extends MessageToMessageCodec<DatagramPacket, Frame> {
  @Override
  protected void encode(ChannelHandlerContext ctx, Frame msg, List<Object> out) throws IOException {
    // As per convention the packet reference count has been incremented
    out.add(msg.packet());
  }

  @Override
  protected void decode(ChannelHandlerContext ctx, DatagramPacket msg, List<Object> out)
      throws IOException {
    // As per convention the packet reference will be incremented in the constructor if needed
    out.add(new Frame(msg));
  }
}
