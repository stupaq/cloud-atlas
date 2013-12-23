package stupaq.cloudatlas.services.busybody.netty;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import stupaq.cloudatlas.messaging.GossipMessage;
import stupaq.cloudatlas.messaging.MessageBus;

public class MessagePostInboundHandler extends SimpleChannelInboundHandler<GossipMessage> {
  private final MessageBus bus;

  public MessagePostInboundHandler(MessageBus bus) {
    this.bus = bus;
  }

  @Override
  protected void channelRead0(ChannelHandlerContext ctx, GossipMessage msg) throws Exception {
    bus.post(msg);
  }
}
