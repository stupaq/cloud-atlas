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
            new DefaultMessageSizeEstimator(GossipingConfigKeys.DATAGRAM_PACKET_MAX_SIZE));
    channel.pipeline()
        .addLast(new DatagramCodec(config))
        .addLast(new GTPHeaderCodec(config))
        .addLast(new FrameCodec(config))
        .addLast(new RetransmissionHandler(config))
        .addLast(new GossipDecoder(config))
        .addLast(new GossipEncoder(config))
        .addLast(new LoggingHandler("BEFORE TIMESTAMPS ADJUSTMENT", LogLevel.TRACE))
        .addLast(new GTPInboundAdjuster(config))
        .addLast(new LoggingHandler("AFTER TIMESTAMPS ADJUSTMENT", LogLevel.TRACE))
        .addLast(new MessageInboundHandler(config));
  }
}
