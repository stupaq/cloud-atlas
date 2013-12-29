package stupaq.cloudatlas.gossiping.channel;

import java.util.List;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageCodec;
import io.netty.util.ReferenceCountUtil;
import stupaq.cloudatlas.configuration.BootstrapConfiguration;
import stupaq.cloudatlas.gossiping.dataformat.WireFrame;

/** PACKAGE-LOCAL */
class GTPHeaderCodec extends MessageToMessageCodec<WireFrame, WireFrame> {
  public GTPHeaderCodec(BootstrapConfiguration config) {
  }

  @Override
  protected void encode(ChannelHandlerContext ctx, WireFrame msg, List<Object> out)
      throws Exception {
    ReferenceCountUtil.retain(msg);
    out.add(msg);
  }

  @Override
  protected void decode(ChannelHandlerContext ctx, WireFrame msg, List<Object> out)
      throws Exception {
    ReferenceCountUtil.retain(msg);
    out.add(msg);
  }
}
