package stupaq.cloudatlas.services.busybody;

import com.google.common.base.Preconditions;
import com.google.common.collect.Sets;
import com.google.common.eventbus.Subscribe;
import com.google.common.util.concurrent.AbstractScheduledService;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Set;
import java.util.concurrent.ScheduledExecutorService;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.util.internal.logging.CommonsLoggerFactory;
import io.netty.util.internal.logging.InternalLoggerFactory;
import stupaq.cloudatlas.attribute.values.CAContact;
import stupaq.cloudatlas.configuration.BootstrapConfiguration;
import stupaq.cloudatlas.configuration.StartIfPresent;
import stupaq.cloudatlas.gossiping.channel.ChannelInitializer;
import stupaq.cloudatlas.messaging.MessageBus;
import stupaq.cloudatlas.messaging.MessageListener;
import stupaq.cloudatlas.messaging.MessageListener.AbstractMessageListener;
import stupaq.cloudatlas.messaging.messages.ContactSelectionMessage;
import stupaq.cloudatlas.messaging.messages.gossips.Gossip;
import stupaq.cloudatlas.messaging.messages.gossips.InboundGossip;
import stupaq.cloudatlas.messaging.messages.gossips.OutboundGossip;
import stupaq.cloudatlas.services.busybody.sessions.SessionId;
import stupaq.cloudatlas.services.busybody.strategies.ContactSelection;
import stupaq.commons.util.concurrent.AsynchronousInvoker.DirectInvocation;
import stupaq.commons.util.concurrent.FastStartScheduler;
import stupaq.commons.util.concurrent.SingleThreadedExecutor;

@StartIfPresent(section = "gossiping")
public class Busybody extends AbstractScheduledService implements BusybodyConfigKeys {
  static {
    InternalLoggerFactory.setDefaultFactory(new CommonsLoggerFactory());
  }

  private static final Log LOG = LogFactory.getLog(Busybody.class);
  private final BootstrapConfiguration config;
  private final SingleThreadedExecutor executor;
  private final MessageBus bus;
  private final Set<CAContact> blacklisted = Sets.newHashSet();
  private NioEventLoopGroup group;
  private Channel channel;
  private CAContact contactSelf;
  private SessionId nextSessionId = new SessionId();

  public Busybody(BootstrapConfiguration config) {
    config.mustContain(BIND_PORT);
    this.config = config;
    bus = config.bus();
    executor = config.threadModel().singleThreaded(Busybody.class);
  }

  @Override
  protected void runOneIteration() throws Exception {
    // ZoneManager will determine contact following provided strategy
    try {
      bus.post(
          new ContactSelectionMessage(ContactSelection.create(config, blacklisted), nextSessionId));
    } finally {
      nextSessionId = nextSessionId.nextSession();
    }
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
        .addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE)
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
    @DirectInvocation
    public void receiveGossip(InboundGossip message);

    @Subscribe
    @DirectInvocation
    public void sendGossip(OutboundGossip message);
  }

  /**
   * Under no circumstance this class can access service state due to {@link DirectInvocation}
   * annotation of subscribing methods.
   */
  private class BusybodyListener extends AbstractMessageListener implements BusybodyContract {
    protected BusybodyListener() {
      super(executor, BusybodyContract.class);
    }

    @Override
    public void receiveGossip(InboundGossip message) {
      Gossip gossip = message.gossip();
      Preconditions.checkNotNull(gossip.sender());
      Preconditions.checkNotNull(gossip.id());
      bus.post(gossip);
    }

    @Override
    public void sendGossip(OutboundGossip message) {
      Gossip gossip = message.gossip();
      Preconditions.checkNotNull(gossip.id());
      gossip.sender(contactSelf);
      channel.writeAndFlush(message);
    }
  }
}
