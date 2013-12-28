package stupaq.cloudatlas.gossiping.channel;

import com.google.common.cache.CacheLoader;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.List;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageCodec;
import io.netty.util.ReferenceCountUtil;
import stupaq.cloudatlas.attribute.values.CAContact;
import stupaq.cloudatlas.configuration.BootstrapConfiguration;
import stupaq.cloudatlas.gossiping.GossipingConfigKeys;
import stupaq.cloudatlas.gossiping.dataformat.WireFrame;
import stupaq.cloudatlas.gossiping.dataformat.WireFrame.FramesBuilder;
import stupaq.cloudatlas.gossiping.dataformat.WireGossip;
import stupaq.cloudatlas.gossiping.peerstate.ContactFrameIndex;
import stupaq.cloudatlas.gossiping.peerstate.ContactStateCache;
import stupaq.cloudatlas.gossiping.peerstate.GossipFrameIndex;
import stupaq.cloudatlas.time.LocalClock;

/** PACKAGE-LOCAL */
final class FrameCodec extends MessageToMessageCodec<WireFrame, WireGossip>
    implements GossipingConfigKeys {
  private static final Log LOG = LogFactory.getLog(FrameCodec.class);
  private final ContactStateCache<ContactFrameIndex> contacts;
  private final LocalClock clock;

  public FrameCodec(final BootstrapConfiguration config) {
    contacts = new ContactStateCache<>(config, new CacheLoader<CAContact, ContactFrameIndex>() {
      @Override
      public ContactFrameIndex load(CAContact key) {
        return new ContactFrameIndex(config);
      }
    });
    clock = config.clock();
  }

  @Override
  protected void encode(ChannelHandlerContext ctx, WireGossip msg, List<Object> out) {
    ByteBuf data = null;
    try {
      data = msg.data();
      int msgLength = data.readableBytes();
      int frameDataSize = DATA_MAX_SIZE;
      int framesCount = (msgLength + frameDataSize - 1) / frameDataSize;
      FramesBuilder builder = new FramesBuilder(clock, msg.contact(), msg.gossipId(), framesCount);
      for (; framesCount > 0; framesCount--) {
        out.add(builder.nextFrame(data.readSlice(Math.min(frameDataSize, data.readableBytes()))));
      }
    } catch (Throwable t) {
      LOG.error("Encoding failed", t);
      // Ignore as we do not close the only channel
    } finally {
      ReferenceCountUtil.release(data);
    }
  }

  @Override
  protected void decode(ChannelHandlerContext ctx, WireFrame msg, List<Object> out) {
    GossipFrameIndex gossip = null;
    try {
      CAContact contact = msg.contact();
      ContactFrameIndex info = contacts.get(contact);
      gossip = info.add(msg);
      if (gossip != null) {
        out.add(new WireGossip(contact, msg.frameId().gossipId(), gossip));
      }
    } catch (Throwable t) {
      LOG.error("Decoding failed", t);
      // Ignore as we do not close the only channel
    } finally {
      ReferenceCountUtil.release(gossip);
    }
  }
}
