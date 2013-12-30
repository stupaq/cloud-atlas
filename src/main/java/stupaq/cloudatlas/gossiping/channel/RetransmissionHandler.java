package stupaq.cloudatlas.gossiping.channel;

import com.google.common.base.Preconditions;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.List;
import java.util.concurrent.ExecutionException;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageCodec;
import io.netty.util.ReferenceCountUtil;
import stupaq.cloudatlas.attribute.values.CAContact;
import stupaq.cloudatlas.configuration.BootstrapConfiguration;
import stupaq.cloudatlas.gossiping.GossipingInternalsHelpers;
import stupaq.cloudatlas.gossiping.dataformat.WireGossip;
import stupaq.cloudatlas.gossiping.peerstate.GossipSessionIndex;

/** PACKAGE-LOCAL */
class RetransmissionHandler extends MessageToMessageCodec<WireGossip, WireGossip> {
  private static final Log LOG = LogFactory.getLog(RetransmissionHandler.class);
  private final LoadingCache<CAContact, GossipSessionIndex> contacts;

  public RetransmissionHandler(BootstrapConfiguration config) {
    Preconditions.checkState(!isSharable());
    contacts = GossipingInternalsHelpers.contactsInfoCache(config)
        .build(new CacheLoader<CAContact, GossipSessionIndex>() {
          @Override
          public GossipSessionIndex load(CAContact key) throws Exception {
            return new GossipSessionIndex();
          }
        });
  }

  @Override
  protected void encode(ChannelHandlerContext ctx, WireGossip msg, List<Object> out)
      throws ExecutionException {
    contacts.get(msg.contact()).sending(msg, ctx);
    ReferenceCountUtil.retain(msg);
    out.add(msg);
  }

  @Override
  protected void decode(ChannelHandlerContext ctx, WireGossip msg, List<Object> out)
      throws ExecutionException {
    contacts.get(msg.contact()).received(msg.gossipId());
    ReferenceCountUtil.retain(msg);
    out.add(msg);
  }

  @Override
  public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
    LOG.error("Codec failed", cause);
  }
}
