package stupaq.cloudatlas.services.busybody;

import com.google.common.collect.Sets;
import com.google.common.eventbus.Subscribe;
import com.google.common.util.concurrent.AbstractScheduledService;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Set;
import java.util.concurrent.ScheduledExecutorService;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;
import stupaq.cloudatlas.attribute.values.CAContact;
import stupaq.cloudatlas.configuration.BootstrapConfiguration;
import stupaq.cloudatlas.configuration.StartIfPresent;
import stupaq.cloudatlas.gossiping.pipeline.GossipChannelInitializer;
import stupaq.cloudatlas.messaging.MessageBus;
import stupaq.cloudatlas.messaging.MessageListener;
import stupaq.cloudatlas.messaging.MessageListener.AbstractMessageListener;
import stupaq.cloudatlas.messaging.messages.ContactSelectionMessage;
import stupaq.cloudatlas.messaging.messages.gossips.InboundGossip;
import stupaq.cloudatlas.messaging.messages.gossips.OutboundGossip;
import stupaq.cloudatlas.services.busybody.strategies.ContactSelection;
import stupaq.commons.util.concurrent.AsynchronousInvoker.DirectInvocation;
import stupaq.commons.util.concurrent.FastStartScheduler;
import stupaq.commons.util.concurrent.SingleThreadedExecutor;

@StartIfPresent(section = "gossip")
public class Busybody extends AbstractScheduledService implements BusybodyConfigKeys {
  private static final Log LOG = LogFactory.getLog(Busybody.class);
  private final BootstrapConfiguration config;
  private final SingleThreadedExecutor executor;
  private final MessageBus bus;
  private final Set<CAContact> blacklisted = Sets.newHashSet();
  private NioEventLoopGroup group;
  private Channel channel;
  private CAContact contactSelf;

  public Busybody(BootstrapConfiguration config) {
    config.mustContain(BIND_PORT);
    this.config = config;
    bus = config.bus();
    executor = config.threadModel().singleThreaded(Busybody.class);
  }

  @Override
  protected void runOneIteration() throws Exception {
    // ZoneManager will determine contact following provided strategy
    bus.post(
        new ContactSelectionMessage(ContactSelection.create(config, blacklisted), contactSelf));
  }

  @Override
  protected void startUp() {
    // Bootstrap Netty
    group = new NioEventLoopGroup();
    contactSelf = new CAContact("127.0.0.1:" + config.getInt(BIND_PORT));
    channel = new Bootstrap().group(group)
        .channel(NioDatagramChannel.class)
        .handler(new GossipChannelInitializer(config))
        .bind(contactSelf.socketAddress())
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
    public void sendGossip(OutboundGossip gossip);

    @Subscribe
    @DirectInvocation
    public void receiveGossip(InboundGossip gossip);
  }

  private class BusybodyListener extends AbstractMessageListener implements BusybodyContract {
    protected BusybodyListener() {
      super(executor, BusybodyContract.class);
    }

    @Override
    public void sendGossip(OutboundGossip gossip) {
      channel.writeAndFlush(gossip);
    }

    @Override
    public void receiveGossip(InboundGossip gossip) {
      bus.post(gossip.getGossip());
    }
  }
}
