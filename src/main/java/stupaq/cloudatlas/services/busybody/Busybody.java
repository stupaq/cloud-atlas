package stupaq.cloudatlas.services.busybody;

import com.google.common.eventbus.Subscribe;
import com.google.common.util.concurrent.AbstractScheduledService;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.DefaultMessageSizeEstimator;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;
import stupaq.cloudatlas.configuration.BootstrapConfiguration;
import stupaq.cloudatlas.configuration.StartIfPresent;
import stupaq.cloudatlas.messaging.MessageBus;
import stupaq.cloudatlas.messaging.MessageListener;
import stupaq.cloudatlas.messaging.MessageListener.AbstractMessageListener;
import stupaq.cloudatlas.messaging.messages.ContactSelectionMessage;
import stupaq.cloudatlas.messaging.messages.gossips.InboundGossip;
import stupaq.cloudatlas.messaging.messages.gossips.OutboundGossip;
import stupaq.cloudatlas.services.busybody.pipeline.GossipChannelInitializer;
import stupaq.cloudatlas.services.busybody.strategies.ContactSelection;
import stupaq.commons.util.concurrent.AsynchronousInvoker.DirectInvocation;
import stupaq.commons.util.concurrent.SingleThreadedExecutor;

@StartIfPresent(section = "gossip")
public class Busybody extends AbstractScheduledService implements BusybodyConfigKeys {
  private static final Log LOG = LogFactory.getLog(Busybody.class);
  private final BootstrapConfiguration config;
  private final SingleThreadedExecutor executor;
  private final MessageBus bus;
  private NioEventLoopGroup group;
  private Channel channel;

  public Busybody(BootstrapConfiguration config) {
    config.mustContain(BIND_PORT);
    this.config = config;
    bus = config.bus();
    executor = config.threadModel().singleThreaded(Busybody.class);
  }

  @Override
  protected void runOneIteration() throws Exception {
    // ZoneManager will determine contact following provided strategy
    bus.post(new ContactSelectionMessage(ContactSelection.create(config)));
  }

  @Override
  protected void startUp() {
    // Bootstrap Netty
    group = new NioEventLoopGroup();
    channel = new Bootstrap().group(group)
        .channel(NioDatagramChannel.class)
        .option(ChannelOption.MESSAGE_SIZE_ESTIMATOR,
            new DefaultMessageSizeEstimator(MESSAGE_SIZE_DEFAULT))
        .handler(new GossipChannelInitializer(config))
        .bind(config.getInt(BIND_PORT))
        .syncUninterruptibly()
        .channel();
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
    return new CustomScheduler() {
      @Override
      protected Schedule getNextSchedule() throws Exception {
        return new Schedule(config.getLong(GOSSIP_PERIOD, GOSSIP_PERIOD_DEFAULT),
            TimeUnit.MILLISECONDS);
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
      // TODO
      LOG.info("Sending gossip: " + gossip);
    }

    @Override
    public void receiveGossip(InboundGossip gossip) {
      // TODO
      LOG.info("Receiving gossip: " + gossip);
    }
  }
}
