package stupaq.cloudatlas.gossiping.channel;

import io.netty.channel.DefaultMessageSizeEstimator;
import io.netty.channel.socket.DatagramChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import stupaq.cloudatlas.configuration.BootstrapConfiguration;
import stupaq.cloudatlas.gossiping.GossipingConfigKeys;

public class ChannelInitializer extends io.netty.channel.ChannelInitializer<DatagramChannel> {
  private final BootstrapConfiguration config;

  public ChannelInitializer(BootstrapConfiguration config) {
    this.config = config;
  }

  @Override
  protected void initChannel(DatagramChannel channel) throws Exception {
    channel.config()
        .setMessageSizeEstimator(
            new DefaultMessageSizeEstimator(GossipingConfigKeys.DATAGRAM_MAX_SIZE));
    channel.pipeline()
        .addLast(new DatagramCodec(config))
        .addLast(new FrameCodec(config))
        .addLast(new GossipCodec(config))
        .addLast(new LoggingHandler(LogLevel.TRACE))
        .addLast(new MessageInboundHandler(config));
  }
}
