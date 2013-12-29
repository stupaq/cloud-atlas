package stupaq.cloudatlas.gossiping.channel;

import com.google.common.base.Preconditions;
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
import stupaq.cloudatlas.gossiping.dataformat.FrameId;
import stupaq.cloudatlas.gossiping.dataformat.WireFrame;
import stupaq.cloudatlas.gossiping.dataformat.WireGossip;
import stupaq.cloudatlas.gossiping.peerstate.ContactFrameIndex;
import stupaq.cloudatlas.gossiping.peerstate.ContactStateCache;
import stupaq.cloudatlas.gossiping.peerstate.GossipFrameIndex;

/** PACKAGE-LOCAL */
class FrameCodec extends MessageToMessageCodec<WireFrame, WireGossip>
    implements GossipingConfigKeys {
  private static final Log LOG = LogFactory.getLog(FrameCodec.class);
  private final ContactStateCache<ContactFrameIndex> contacts;

  public FrameCodec(final BootstrapConfiguration config) {
    Preconditions.checkState(!isSharable());
    contacts = new ContactStateCache<>(config, new CacheLoader<CAContact, ContactFrameIndex>() {
      @Override
      public ContactFrameIndex load(CAContact key) {
        return new ContactFrameIndex(config);
      }
    });
  }

  @Override
  protected void encode(ChannelHandlerContext ctx, WireGossip msg, List<Object> out) {
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
