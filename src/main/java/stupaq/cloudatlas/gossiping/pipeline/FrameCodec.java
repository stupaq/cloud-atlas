package stupaq.cloudatlas.gossiping.pipeline;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageCodec;
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
  private final LoadingCache<CAContact, ContactInfo> contacts;

  public FrameCodec(final CAConfiguration config) {
    contacts = CacheBuilder.newBuilder()
        .removalListener(new ReferenceCountedRemovalListener())
        .expireAfterAccess(config.getLong(CONTACT_INFO_RETENTION, CONTACT_INFO_RETENTION_DEFAULT),
            TimeUnit.MILLISECONDS)
        .build(new CacheLoader<CAContact, ContactInfo>() {
          @Override
          public ContactInfo load(CAContact key) {
            return new ContactInfo(config);
          }
        });
  }

  @Override
  protected void encode(ChannelHandlerContext ctx, EncodedGossip msg, List<Object> out)
      throws ExecutionException {
    ContactInfo info = contacts.get(msg.contact());
    InetSocketAddress address = msg.contact().socketAddress();
    ByteBuf data = msg.data();
    try {
      int msgLength = data.readableBytes();
      int framesCount = (msgLength + DATA_MAX_SIZE - 1) / DATA_MAX_SIZE;
      FrameId frameId = info.nextGossipId(framesCount).first();
      // FIXME
    } finally {
      data.release();
    }
  }

  @Override
  protected void decode(ChannelHandlerContext ctx, Frame msg, List<Object> out) throws Exception {
    CAContact contact = msg.sender();
    ContactInfo info = contacts.get(contact);
    GossipInfo gossip = info.add(msg);
    if (gossip != null) {
      try {
        out.add(new EncodedGossip(contact, gossip));
      } finally {
        gossip.release();
      }
    }
  }
}
