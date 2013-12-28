package stupaq.cloudatlas.gossiping.channel;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import stupaq.cloudatlas.configuration.BootstrapConfiguration;
import stupaq.cloudatlas.messaging.MessageBus;
import stupaq.cloudatlas.messaging.messages.gossips.InboundGossip;

/** PACKAGE-LOCAL */
class MessageInboundHandler extends SimpleChannelInboundHandler<InboundGossip> {
  private final MessageBus bus;

  public MessageInboundHandler(BootstrapConfiguration config) {
    this.bus = config.bus();
  }

  @Override
  protected void channelRead0(ChannelHandlerContext ctx, InboundGossip msg) throws Exception {
    bus.post(msg);
  }
}
