package stupaq.cloudatlas.gossiping.channel;

import com.google.common.base.Preconditions;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.DatagramPacket;
import io.netty.handler.codec.MessageToMessageCodec;
import io.netty.util.ReferenceCountUtil;
import stupaq.cloudatlas.configuration.BootstrapConfiguration;
import stupaq.cloudatlas.gossiping.GossipingInternalsConfigKeys;
import stupaq.cloudatlas.gossiping.dataformat.WireFrame;
import stupaq.cloudatlas.gossiping.dataformat.WireGTPHeader;
import stupaq.cloudatlas.time.GTPSynchronizedClock;
import stupaq.compact.CompactInput;
import stupaq.compact.CompactOutput;

/** PACKAGE-LOCAL */
class GTPHeaderCodec extends MessageToMessageCodec<DatagramPacket, DatagramPacket>
    implements GossipingInternalsConfigKeys {
  private static final Log LOG = LogFactory.getLog(GTPHeaderCodec.class);
  private final GTPSynchronizedClock clock;
  private final LoadingCache<InetSocketAddress, WireGTPHeader> pendingResponses;

  public GTPHeaderCodec(BootstrapConfiguration config) {
    clock = config.synchronizedClock();
    pendingResponses = CacheBuilder.newBuilder()
        .expireAfterAccess(
            config.getLong(GTP_PENDING_RESPONSE_RETENTION, GTP_PENDING_RESPONSE_RETENTION_DEFAULT),
            TimeUnit.MILLISECONDS)
        .build(new CacheLoader<InetSocketAddress, WireGTPHeader>() {
          @Override
          public WireGTPHeader load(InetSocketAddress key) throws Exception {
            return new WireGTPHeader();
          }
        });
  }

  @Override
  protected void encode(ChannelHandlerContext ctx, DatagramPacket msg, List<Object> out)
      throws IOException, ExecutionException {
    Preconditions.checkNotNull(msg.recipient());
    // We attach empty GTP headers all but first frames (in a gossip).
    ByteBuf headerBuf = WireGTPHeader.SERIALIZED_EMPTY;
    try {
      // We attach non-empty GTP headers to the first frame in each gossip only.
      if (WireFrame.sneakPeakIsFirst(msg.content())) {
        WireGTPHeader header = pendingResponses.get(msg.recipient());
        // Remove the header, it will be served.
        pendingResponses.invalidate(msg.recipient());
        // The header must be either empty (that means a new one) or half empty,
        // in both cases it needs our timestamp.
        header.record(clock.timestamp());
        headerBuf = Unpooled.buffer(WireGTPHeader.SERIALIZED_MAX_SIZE);
        try (CompactOutput output = new CompactOutput(new ByteBufOutputStream(headerBuf))) {
          WireGTPHeader.SERIALIZER.writeInstance(output, header);
        }
      }
      // Forward datagram with altered content, we have to bump both buffers' reference counters
      // since one will be released below and the other by Netty.
      out.add(new DatagramPacket(Unpooled.wrappedBuffer(headerBuf.retain(), msg.content().retain()),
          msg.recipient()));
    } finally {
      ReferenceCountUtil.release(headerBuf);
    }
  }

  @Override
  protected void decode(ChannelHandlerContext ctx, DatagramPacket msg, List<Object> out)
      throws IOException {
    Preconditions.checkNotNull(msg.sender());
    WireGTPHeader header;
    try (CompactInput input = new CompactInput(new ByteBufInputStream(msg.content()))) {
      header = WireGTPHeader.SERIALIZER.readInstance(input);
    }
    // Empty header means that the frame is not GTP carrier.
    if (!header.is(WireGTPHeader.EMPTY)) {
      // Note that upon adding our timestamp the header cannot be ready.
      header.record(clock.timestamp());
      // Ready header can be extracted and returned since we need only three messages to
      // synchronize both nodes - we can use last two timestamps from the full header.
      if (header.is(WireGTPHeader.READY)) {
        clock.record(msg.sender(), header.extractAndFlip());
      }
      // Half full header will be returned to it's origin with the next carrier.
      // This branch handles described headers recycling.
      if (header.is(WireGTPHeader.HALF)) {
        pendingResponses.put(msg.sender(), header);
      }
    }
    // Forward datagram with read (skipped) header.
    ReferenceCountUtil.retain(msg);
    out.add(msg);
  }

  @Override
  public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
    LOG.error("Codec failed", cause);
  }
}
