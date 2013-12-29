package stupaq.cloudatlas.gossiping.channel;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.DatagramPacket;
import io.netty.handler.codec.MessageToMessageCodec;
import io.netty.util.ReferenceCountUtil;
import stupaq.cloudatlas.attribute.values.CAContact;
import stupaq.cloudatlas.configuration.BootstrapConfiguration;
import stupaq.cloudatlas.gossiping.GossipingConfigKeys;
import stupaq.cloudatlas.gossiping.dataformat.WireFrame;
import stupaq.cloudatlas.gossiping.dataformat.WireGTPHeader;
import stupaq.cloudatlas.time.LocalClock;

/** PACKAGE-LOCAL */
class GTPHeaderCodec extends MessageToMessageCodec<DatagramPacket, DatagramPacket>
    implements GossipingConfigKeys {
  private static final Log LOG = LogFactory.getLog(GTPHeaderCodec.class);
  private final LocalClock clock;
  private final LoadingCache<CAContact, WireGTPHeader> pendingResponses;

  public GTPHeaderCodec(BootstrapConfiguration config) {
    clock = config.clock();
    pendingResponses = CacheBuilder.newBuilder()
        .expireAfterAccess(
            config.getLong(GTP_PENDING_RESPONSE_RETENTION, GTP_PENDING_RESPONSE_RETENTION_DEFAULT),
            TimeUnit.MILLISECONDS)
        .build(new CacheLoader<CAContact, WireGTPHeader>() {
          @Override
          public WireGTPHeader load(CAContact key) throws Exception {
            return new WireGTPHeader();
          }
        });
  }

  @Override
  protected void encode(ChannelHandlerContext ctx, DatagramPacket msg, List<Object> out)
      throws IOException {
    // We attach empty GTP headers all but first frames (in a gossip)
    ByteBuf header = WireGTPHeader.SERIALIZED_EMPTY;
    try {
      // We attach non-empty GTP headers to the first frame in each gossip only
      if (WireFrame.sneakPeakIsFirst(msg.content())) {
        // FIXME
      }
      // Forward datagram with altered content
      out.add(new DatagramPacket(Unpooled.wrappedBuffer(header.retain(), msg.content().retain()),
          msg.recipient()));
    } finally {
      ReferenceCountUtil.release(header);
    }
  }

  @Override
  protected void decode(ChannelHandlerContext ctx, DatagramPacket msg, List<Object> out) {
    // FIXME
    msg.content().skipBytes(WireGTPHeader.SERIALIZED_EMPTY.readableBytes());
    // Forward datagram with altered content
    ReferenceCountUtil.retain(msg);
    out.add(msg);
  }

  @Override
  public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
    LOG.error("Codec failed", cause);
  }
}
