package stupaq.cloudatlas.services.busybody.netty;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import stupaq.cloudatlas.configuration.BootstrapConfiguration;

public class GossipChannelInitializer extends ChannelInitializer<SocketChannel> {
  private final BootstrapConfiguration config;

  public GossipChannelInitializer(BootstrapConfiguration config) {
    this.config = config;
  }

  @Override
  protected void initChannel(SocketChannel channel) throws Exception {
    channel.pipeline()
        .addLast(new CompactSerializableEncoder())
        .addLast(new CompactSerializableDecoder())
        .addLast(new MessagePostInboundHandler(config.bus()));
  }
}
