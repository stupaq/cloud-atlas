package stupaq.cloudatlas.services.busybody;

import com.google.common.base.Preconditions;
import com.google.common.base.Supplier;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.Sets;
import com.google.common.eventbus.Subscribe;
import com.google.common.util.concurrent.AbstractScheduledService;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Set;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.util.internal.logging.CommonsLoggerFactory;
import io.netty.util.internal.logging.InternalLoggerFactory;
import stupaq.cloudatlas.attribute.values.CAContact;
import stupaq.cloudatlas.configuration.BootstrapConfiguration;
import stupaq.cloudatlas.configuration.StartIfPresent;
import stupaq.cloudatlas.gossiping.GossipingInternalsConfigKeys;
import stupaq.cloudatlas.gossiping.channel.ChannelInitializer;
import stupaq.cloudatlas.messaging.MessageBus;
import stupaq.cloudatlas.messaging.MessageListener;
import stupaq.cloudatlas.messaging.MessageListener.AbstractMessageListener;
import stupaq.cloudatlas.messaging.messages.ContactSelectionMessage;
import stupaq.cloudatlas.messaging.messages.DuplexGossipingMessage;
import stupaq.cloudatlas.messaging.messages.gossips.Gossip;
import stupaq.cloudatlas.messaging.messages.gossips.InboundGossip;
import stupaq.cloudatlas.messaging.messages.gossips.OutboundGossip;
import stupaq.cloudatlas.messaging.messages.gossips.SessionAcknowledgedGossip;
import stupaq.cloudatlas.messaging.messages.gossips.ZonesInterestInitialGossip;
import stupaq.cloudatlas.messaging.messages.gossips.ZonesUpdateGossip;
import stupaq.cloudatlas.services.busybody.sessions.SessionId;
import stupaq.cloudatlas.services.busybody.strategies.ContactSelection;
import stupaq.commons.cache.CacheSet;
import stupaq.commons.util.concurrent.AsynchronousInvoker.DirectInvocation;
import stupaq.commons.util.concurrent.AsynchronousInvoker.ScheduledInvocation;
import stupaq.commons.util.concurrent.FastStartScheduler;
import stupaq.commons.util.concurrent.SingleThreadAssertion;
import stupaq.commons.util.concurrent.SingleThreadedExecutor;

@StartIfPresent(section = "gossiping")
public class Busybody extends AbstractScheduledService
    implements BusybodyConfigKeys, GossipingInternalsConfigKeys {
  static {
    InternalLoggerFactory.setDefaultFactory(new CommonsLoggerFactory());
  }

  private static final Log LOG = LogFactory.getLog(Busybody.class);
  private final BootstrapConfiguration config;
  private final SingleThreadedExecutor executor;
  private final MessageBus bus;
  private final Set<CAContact> blacklisted = Sets.newHashSet();
  private final CacheSet<CAContact> freshContacts;
  private final Supplier<SessionId> newSession = new Supplier<SessionId>() {
    private final SingleThreadAssertion assertion = new SingleThreadAssertion();
    private SessionId nextSessionId = new SessionId();

    @Override
    public SessionId get() {
      assertion.check();
      SessionId session = nextSessionId;
      nextSessionId = nextSessionId.nextSession();
      return session;
    }
  };
  private NioEventLoopGroup group;
  private Channel channel;
  private CAContact contactSelf;

  public Busybody(BootstrapConfiguration config) {
    config.mustContain(BIND_PORT);
    this.config = config;
    bus = config.bus();
    executor = config.threadModel().singleThreaded(Busybody.class);
    freshContacts = new CacheSet<>(CacheBuilder.newBuilder()
        .expireAfterWrite(config.getLong(CONTACT_FRESH_TIMEOUT,
            config.getLong(GOSSIP_PERIOD, GOSSIP_PERIOD_DEFAULT)), TimeUnit.MILLISECONDS));
  }

  @Override
  protected void runOneIteration() throws Exception {
    // ZoneManager will determine contact following provided strategy
    bus.post(new ContactSelectionMessage(ContactSelection.create(config, blacklisted),
        newSession.get()));
  }

  @Override
  protected void startUp() {
    // Bootstrap Netty
    group = new NioEventLoopGroup();
    contactSelf = config.getLocalContact(config.getInt(BIND_PORT));
    channel = new Bootstrap().group(group)
        .channel(NioDatagramChannel.class)
        .handler(new ChannelInitializer(config))
        .bind(contactSelf.address())
        .addListener(new ChannelFutureListener() {
          @Override
          public void operationComplete(ChannelFuture future) {
            if (!future.isSuccess()) {
              LOG.error("Pipeline failed", future.cause());
            }
          }
        })
        .syncUninterruptibly()
        .channel();
    blacklisted.add(contactSelf);
    LOG.info("Agent's contact information: " + contactSelf);
    // We're ready to operate
    bus.register(new BusybodyListener());
  }

  @Override
  protected void shutDown() {
    // Shut down Netty
    group.shutdownGracefully().awaitUninterruptibly();
    // ...and the service
    config.threadModel().free(executor);
  }

  @Override
  protected Scheduler scheduler() {
    return new FastStartScheduler() {
      @Override
      protected long getNextDelayMs() throws Exception {
        return config.getLong(GOSSIP_PERIOD, GOSSIP_PERIOD_DEFAULT);
      }
    };
  }

  @Override
  protected ScheduledExecutorService executor() {
    return executor;
  }

  private static interface BusybodyContract extends MessageListener {
    @Subscribe
    @ScheduledInvocation
    public void duplexCommunication(ZonesInterestInitialGossip message);

    @Subscribe
    @ScheduledInvocation
    public void contactUpdateNotification(ZonesUpdateGossip message);

    @Subscribe
    @DirectInvocation
    public void receiveGossip(InboundGossip message);

    @Subscribe
    @DirectInvocation
    public void sendGossip(OutboundGossip message);

    @Subscribe
    @DirectInvocation
    public void ignoreAcknowledgment(SessionAcknowledgedGossip message);
  }

  private class BusybodyListener extends AbstractMessageListener implements BusybodyContract {
    protected BusybodyListener() {
      super(executor, BusybodyContract.class);
    }

    @Override
    public void duplexCommunication(ZonesInterestInitialGossip message) {
      // Initial message is sent by the node who initiated communication only
      // to make gossiping two-way we respond with interest message (non-initial version)
      // if we haven't heard from the contact for a configurable period of time.
      if (freshContacts.contains(message.sender())) {
        if (LOG.isInfoEnabled()) {
          LOG.info("Contact: " + message.sender() + " considered fresh, aborting duplex gossiping");
        }
        return;
      }
      bus.post(new DuplexGossipingMessage(message.sender(), newSession.get()));
    }

    @Override
    public void contactUpdateNotification(ZonesUpdateGossip message) {
      // Update from the contact means that we at least spoke with it and we can give up asking
      // it for updates for a moment.
      freshContacts.add(message.sender());
    }

    /** Under no circumstance this method can access service state. */
    @Override
    public void receiveGossip(InboundGossip message) {
      Gossip gossip = message.gossip();
      Preconditions.checkNotNull(gossip.sender());
      Preconditions.checkNotNull(gossip.id());
      bus.post(gossip);
    }

    /** Under no circumstance this method can access service state. */
    @Override
    public void sendGossip(OutboundGossip message) {
      Gossip gossip = message.gossip();
      Preconditions.checkNotNull(gossip.id());
      gossip.sender(contactSelf);
      channel.writeAndFlush(message);
    }

    /** Under no circumstance this method can access service state. */
    @Override
    public void ignoreAcknowledgment(SessionAcknowledgedGossip message) {
      // Ignore, we don't want this message to pop out in the logs.
    }
  }
}
