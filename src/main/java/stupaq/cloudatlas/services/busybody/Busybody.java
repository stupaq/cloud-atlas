package stupaq.cloudatlas.services.busybody;

import com.google.common.util.concurrent.AbstractScheduledService;

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
import stupaq.cloudatlas.services.busybody.netty.GossipChannelInitializer;
import stupaq.commons.util.concurrent.SingleThreadedExecutor;

@StartIfPresent(section = "gossip")
public class Busybody extends AbstractScheduledService implements BusybodyConfigKeys {
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
    // FIXME initiate contact selection
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
    long period = config.getLong(GOSSIP_PERIOD, GOSSIP_PERIOD_DEFAULT);
    return Scheduler.newFixedRateSchedule(period / 10, period, TimeUnit.MILLISECONDS);
  }

  @Override
  protected ScheduledExecutorService executor() {
    return executor;
  }

  private static interface BusybodyContract extends MessageListener {
  }

  private class BusybodyListener extends AbstractMessageListener implements BusybodyContract {
    protected BusybodyListener() {
      super(executor, BusybodyContract.class);
    }
  }
}
