package stupaq.cloudatlas.gossiping.channel;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.IOException;
import java.util.List;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;
import io.netty.util.ReferenceCountUtil;
import stupaq.cloudatlas.attribute.values.CAContact;
import stupaq.cloudatlas.configuration.BootstrapConfiguration;
import stupaq.cloudatlas.gossiping.dataformat.FrameId;
import stupaq.cloudatlas.gossiping.dataformat.WireFrame;
import stupaq.cloudatlas.gossiping.dataformat.WireGossip;

/** PACKAGE-LOCAL */
class FrameEncoder extends MessageToMessageEncoder<WireGossip> {
  private static final Log LOG = LogFactory.getLog(FrameEncoder.class);

  public FrameEncoder(BootstrapConfiguration config) {
  }

  @Override
  protected void encode(ChannelHandlerContext ctx, WireGossip msg, List<Object> out)
      throws IOException {
    CAContact destination = msg.contact();
    ByteBuf data = null;
    try {
      data = msg.data();
      int framesCount = WireFrame.howManyFrames(data.readableBytes());
      for (FrameId frameId : msg.gossipId().frames(framesCount)) {
        int bytes = Math.min(WireFrame.frameDataMaxSize(frameId), data.readableBytes());
        out.add(new WireFrame(destination, frameId, data.readSlice(bytes)));
        if (LOG.isTraceEnabled()) {
          LOG.trace("Wrote frame: " + frameId + " data size: " + bytes);
          if (bytes == 0) {
            LOG.error("WROTE EMPTY FRAME!");
          }
        }
      }
    } finally {
      ReferenceCountUtil.release(data);
    }
  }

  @Override
  public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
    LOG.error("Decoding failed", cause);
  }
}
