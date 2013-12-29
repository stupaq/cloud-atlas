package stupaq.cloudatlas.gossiping.channel;

import com.google.common.base.Preconditions;
import com.google.common.cache.CacheLoader;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.List;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.netty.util.ReferenceCountUtil;
import stupaq.cloudatlas.attribute.values.CAContact;
import stupaq.cloudatlas.configuration.BootstrapConfiguration;
import stupaq.cloudatlas.gossiping.GossipingConfigKeys;
import stupaq.cloudatlas.gossiping.dataformat.WireFrame;
import stupaq.cloudatlas.gossiping.dataformat.WireGossip;
import stupaq.cloudatlas.gossiping.peerstate.ContactFrameIndex;
import stupaq.cloudatlas.gossiping.peerstate.ContactStateCache;
import stupaq.cloudatlas.gossiping.peerstate.GossipFrameIndex;

/** PACKAGE-LOCAL */
class FrameDecoder extends MessageToMessageDecoder<WireFrame> implements GossipingConfigKeys {
  private static final Log LOG = LogFactory.getLog(FrameDecoder.class);
  private final ContactStateCache<ContactFrameIndex> contacts;

  public FrameDecoder(final BootstrapConfiguration config) {
    Preconditions.checkState(!isSharable());
    contacts = new ContactStateCache<>(config, new CacheLoader<CAContact, ContactFrameIndex>() {
      @Override
      public ContactFrameIndex load(CAContact key) {
        return new ContactFrameIndex(config);
      }
    });
  }

  @Override
  protected void decode(ChannelHandlerContext ctx, WireFrame msg, List<Object> out)
      throws Exception {
    GossipFrameIndex gossip = null;
    try {
      CAContact contact = msg.contact();
      ContactFrameIndex info = contacts.get(contact);
      gossip = info.add(msg);
      if (gossip != null) {
        out.add(new WireGossip(contact, msg.frameId().gossipId(), gossip));
      }
    } finally {
      ReferenceCountUtil.release(gossip);
    }
  }

  @Override
  public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
    LOG.error("Decoding failed", cause);
  }
}