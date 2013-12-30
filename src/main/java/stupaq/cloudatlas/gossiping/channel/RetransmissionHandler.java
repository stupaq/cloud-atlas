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
import stupaq.cloudatlas.gossiping.peerstate.SessionRetransmissions;
import stupaq.cloudatlas.services.busybody.BusybodyConfigKeys;

/** PACKAGE-LOCAL */
class RetransmissionHandler extends MessageToMessageCodec<WireGossip, WireGossip>
    implements BusybodyConfigKeys {
  private static final Log LOG = LogFactory.getLog(RetransmissionHandler.class);
  private final LoadingCache<CAContact, SessionRetransmissions> contacts;

  public RetransmissionHandler(final BootstrapConfiguration config) {
    Preconditions.checkState(!isSharable());
    int retryCount = config.getInt(GOSSIP_RETRY_COUNT, GOSSIP_RETRY_COUNT_DEFAULT);
    if (retryCount > 0) {
      contacts = GossipingInternalsHelpers.contactsInfoCache(config)
          .build(new CacheLoader<CAContact, SessionRetransmissions>() {
            @Override
            public SessionRetransmissions load(CAContact key) throws Exception {
              return new SessionRetransmissions(config);
            }
          });
    } else {
      contacts = null;
      LOG.warn("Retry count set to 0, retransmission handler disabled");
    }
  }

  @Override
  protected void encode(ChannelHandlerContext ctx, WireGossip msg, List<Object> out)
      throws ExecutionException {
    if (contacts != null) {
      try {
        // msg.contact() point to the recipient of the message
        contacts.get(msg.contact()).sending(ctx, msg);
      } catch (Throwable t) {
        LOG.error("Gossip: " + msg.gossipId() + " will not be retransmitted", t);
      }
    }
    ReferenceCountUtil.retain(msg);
    out.add(msg);
  }

  @Override
  protected void decode(ChannelHandlerContext ctx, WireGossip msg, List<Object> out)
      throws ExecutionException {
    if (contacts != null) {
      try {
        // msg.contact() point to the sender of the message
        contacts.get(msg.contact()).received(msg.gossipId());
      } catch (Throwable t) {
        LOG.error("Failed to acknowledge with gossip: " + msg.gossipId(), t);
      }
    }
    ReferenceCountUtil.retain(msg);
    out.add(msg);
  }

  @Override
  public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
    LOG.error("Codec failed", cause);
  }
}
