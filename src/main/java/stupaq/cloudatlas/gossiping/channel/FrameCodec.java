package stupaq.cloudatlas.gossiping.channel;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageCodec;
import io.netty.util.ReferenceCountUtil;
import stupaq.cloudatlas.attribute.values.CAContact;
import stupaq.cloudatlas.configuration.CAConfiguration;
import stupaq.cloudatlas.gossiping.GossipingConfigKeys;
import stupaq.cloudatlas.gossiping.dataformat.EncodedGossip;
import stupaq.cloudatlas.gossiping.dataformat.Frame;
import stupaq.cloudatlas.gossiping.dataformat.FrameId;
import stupaq.cloudatlas.gossiping.sessions.ContactInfo;
import stupaq.cloudatlas.gossiping.sessions.GossipInfo;
import stupaq.commons.cache.ReferenceCountedRemovalListener;

import static stupaq.cloudatlas.gossiping.dataformat.Frame.DATA_MAX_SIZE;

/** PACKAGE-LOCAL */
final class FrameCodec extends MessageToMessageCodec<Frame, EncodedGossip>
    implements GossipingConfigKeys {
  private static final Log LOG = LogFactory.getLog(FrameCodec.class);
  private final LoadingCache<CAContact, ContactInfo> contacts;

  public FrameCodec(final CAConfiguration config) {
    contacts = CacheBuilder.newBuilder()
        .expireAfterAccess(config.getLong(CONTACT_INFO_RETENTION, CONTACT_INFO_RETENTION_DEFAULT),
            TimeUnit.MILLISECONDS)
        .removalListener(new ReferenceCountedRemovalListener())
        .build(new CacheLoader<CAContact, ContactInfo>() {
          @Override
          public ContactInfo load(CAContact key) {
            return new ContactInfo(config);
          }
        });
  }

  @Override
  protected void encode(ChannelHandlerContext ctx, EncodedGossip msg, List<Object> out) {
    ByteBuf data = null;
    try {
      ContactInfo info = contacts.get(msg.contact());
      InetSocketAddress address = msg.contact().socketAddress();
      data = msg.data();
      int msgLength = data.readableBytes();
      int frameDataSize = DATA_MAX_SIZE;
      int framesCount = (msgLength + frameDataSize - 1) / frameDataSize;
      FrameId frameId = info.nextGossipId(framesCount).first();
      for (; framesCount > 0; framesCount--, frameId = frameId.next()) {
        out.add(new Frame(frameId, address,
            data.readSlice(Math.min(frameDataSize, data.readableBytes()))));
      }
    } catch (Throwable t) {
      LOG.error("Encoding failed", t);
      // Ignore as we do not close the only channel
    } finally {
      ReferenceCountUtil.release(data);
    }
  }

  @Override
  protected void decode(ChannelHandlerContext ctx, Frame msg, List<Object> out) {
    GossipInfo gossip = null;
    try {
      CAContact contact = msg.sender();
      ContactInfo info = contacts.get(contact);
      gossip = info.add(msg);
      if (gossip != null) {
        out.add(new EncodedGossip(contact, gossip));
      }
    } catch (Throwable t) {
      LOG.error("Decoding failed", t);
      // Ignore as we do not close the only channel
    } finally {
      ReferenceCountUtil.release(gossip);
    }
  }
}
