package stupaq.cloudatlas.gossiping.channel;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.DefaultMessageSizeEstimator;
import io.netty.channel.socket.DatagramChannel;
import io.netty.handler.logging.LoggingHandler;
import stupaq.cloudatlas.configuration.BootstrapConfiguration;
import stupaq.cloudatlas.gossiping.dataformat.Frame;

public class GossipChannelInitializer extends ChannelInitializer<DatagramChannel> {
  private final BootstrapConfiguration config;

  public GossipChannelInitializer(BootstrapConfiguration config) {
    this.config = config;
  }

  @Override
  protected void initChannel(DatagramChannel channel) throws Exception {
    channel.config()
        .setMessageSizeEstimator(new DefaultMessageSizeEstimator(Frame.DATAGRAM_MAX_SIZE));
    channel.pipeline()
        .addLast(new LoggingHandler("Pre wire"))
        .addLast(new DatagramCodec())
        .addLast(new LoggingHandler("Pre datagram"))
        .addLast(new FrameCodec(config))
        .addLast(new LoggingHandler("Pre frame"))
        .addLast(new GossipCodec())
        .addLast(new LoggingHandler("Pre codec"))
        .addLast(new MessageInboundHandler(config.bus()));
  }
}
