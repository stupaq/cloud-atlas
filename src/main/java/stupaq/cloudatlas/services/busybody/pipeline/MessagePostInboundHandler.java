package stupaq.cloudatlas.services.busybody.pipeline;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import stupaq.cloudatlas.messaging.messages.Gossip;
import stupaq.cloudatlas.messaging.MessageBus;

public class MessagePostInboundHandler extends SimpleChannelInboundHandler<Gossip> {
  private final MessageBus bus;

  public MessagePostInboundHandler(MessageBus bus) {
    this.bus = bus;
  }

  @Override
  protected void channelRead0(ChannelHandlerContext ctx, Gossip msg) throws Exception {
    bus.post(msg);
  }
}
