package stupaq.cloudatlas.services.busybody.netty;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.DatagramChannel;
import stupaq.cloudatlas.configuration.BootstrapConfiguration;

public class GossipChannelInitializer extends ChannelInitializer<DatagramChannel> {
  private final BootstrapConfiguration config;

  public GossipChannelInitializer(BootstrapConfiguration config) {
    this.config = config;
  }

  @Override
  protected void initChannel(DatagramChannel channel) throws Exception {
    channel.pipeline()
        .addLast(new CompactSerializableEncoder())
        .addLast(new CompactSerializableDecoder())
        .addLast(new MessagePostInboundHandler(config.bus()));
  }
}
