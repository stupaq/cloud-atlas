package stupaq.cloudatlas.gossiping.peerstate;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.cache.RemovalListener;
import com.google.common.cache.RemovalNotification;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import io.netty.channel.ChannelHandlerContext;
import io.netty.util.AbstractReferenceCounted;
import stupaq.cloudatlas.configuration.BootstrapConfiguration;
import stupaq.cloudatlas.gossiping.dataformat.GossipId;
import stupaq.cloudatlas.gossiping.dataformat.WireGossip;
import stupaq.cloudatlas.services.busybody.BusybodyConfigKeys;
import stupaq.cloudatlas.services.busybody.sessions.SessionId;

import static java.util.Collections.synchronizedList;
import static stupaq.commons.util.concurrent.LimitedRepeatsRunnable.scheduleAtFixedRate;

public class SessionRetransmissions implements BusybodyConfigKeys {
  private static final Log LOG = LogFactory.getLog(SessionRetransmissions.class);
  private final LoadingCache<SessionId, List<PendingGossip>> sessions;
  private final int retryCount;
  private final long retryDelay;

  public SessionRetransmissions(BootstrapConfiguration config) {
    // We can safely assume that retryCount > 0.
    retryCount = config.getInt(GOSSIP_RETRY_COUNT, GOSSIP_RETRY_COUNT_DEFAULT);
    retryDelay = config.getLong(GOSSIP_RETRY_DELAY, GOSSIP_RETRY_DELAY_DEFAULT);
    sessions = CacheBuilder.newBuilder()
        .expireAfterAccess(retryDelay * (retryCount + 1), TimeUnit.MILLISECONDS)
        .removalListener(new RemovalListener<SessionId, List<PendingGossip>>() {
          @Override
          public void onRemoval(RemovalNotification<SessionId, List<PendingGossip>> notification) {
            List<PendingGossip> pendingGossips = notification.getValue();
            if (pendingGossips != null) {
              Iterator<PendingGossip> iterator = pendingGossips.iterator();
              while (iterator.hasNext()) {
                iterator.next().release();
                iterator.remove();
              }
            }
          }
        })
        .build(new CacheLoader<SessionId, List<PendingGossip>>() {
          @Override
          public List<PendingGossip> load(SessionId key) {
            return synchronizedList(new ArrayList<PendingGossip>());
          }
        });
  }

  public void received(GossipId receivedId) {
    List<PendingGossip> entries = sessions.getIfPresent(receivedId.sessionId());
    if (entries != null) {
      Iterator<PendingGossip> iterator = entries.iterator();
      while (iterator.hasNext()) {
        PendingGossip pending = iterator.next();
        // We can have two sessions with the same id between a pair of nodes - that's not a problem.
        // When acknowledging outbound messages we have to take into account inbound messages that
        // were generated in response to the outbound ones.
        if (receivedId.isResponseTo(pending.gossipId())) {
          if (LOG.isTraceEnabled()) {
            LOG.trace("Acknowledged gossip: " + pending.gossipId() + " by: " + receivedId);
          }
          pending.release();
          iterator.remove();
        }
      }
    }
  }

  public void sending(final ChannelHandlerContext ctx, final WireGossip msg) {
    List<PendingGossip> entries = sessions.getUnchecked(msg.gossipId().sessionId());
    ScheduledExecutorService executor = ctx.executor();
    ScheduledFuture<?> future = scheduleAtFixedRate(executor, new Runnable() {
      @Override
      public void run() {
        if (LOG.isTraceEnabled()) {
          LOG.trace("Retransmitting gossip: " + msg.gossipId());
        }
        try {
          ctx.writeAndFlush(msg.retain());
        } catch (Throwable t) {
          LOG.error("Retransmission failed", t);
        }
      }
    }, retryCount, retryDelay, retryDelay, TimeUnit.MILLISECONDS);
    // The saved reference resides in pending gossip
    entries.add(new PendingGossip(future, msg));
  }

  private static class PendingGossip extends AbstractReferenceCounted {
    private final ScheduledFuture<?> retransmits;
    private final WireGossip msg;

    public PendingGossip(ScheduledFuture<?> retransmits, WireGossip msg) {
      this.retransmits = retransmits;
      this.msg = msg.retain();
    }

    public GossipId gossipId() {
      return msg.gossipId();
    }

    @Override
    protected void deallocate() {
      msg.release();
      retransmits.cancel(false);
    }
  }
}
