package stupaq.cloudatlas.gossiping.channel;

import io.netty.channel.DefaultMessageSizeEstimator;
import io.netty.channel.socket.DatagramChannel;
import stupaq.cloudatlas.configuration.BootstrapConfiguration;
import stupaq.cloudatlas.gossiping.GossipingInternalsConfigKeys;

public class ChannelInitializer extends io.netty.channel.ChannelInitializer<DatagramChannel> {
  private final BootstrapConfiguration config;

  public ChannelInitializer(BootstrapConfiguration config) {
    this.config = config;
  }

  @Override
  protected void initChannel(DatagramChannel channel) throws Exception {
    channel.config()
        .setMessageSizeEstimator(
            new DefaultMessageSizeEstimator(GossipingInternalsConfigKeys.DATAGRAM_PACKET_MAX_SIZE));
    channel.pipeline()
        .addLast(new GTPHeaderCodec(config))
        .addLast(new DatagramDecoder(config))
        .addLast(new DatagramEncoder(config))
        .addLast(new FrameDecoder(config))
        .addLast(new FrameEncoder(config))
        .addLast(new RetransmissionHandler(config))
        .addLast(new GossipDecoder(config))
        .addLast(new GossipEncoder(config))
        .addLast(new GTPInboundHandler(config))
        .addLast(new MessageInboundHandler(config));
  }
}
