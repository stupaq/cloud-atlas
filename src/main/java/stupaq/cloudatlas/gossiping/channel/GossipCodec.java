package stupaq.cloudatlas.gossiping.channel;

import java.io.IOException;
import java.util.List;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageCodec;
import stupaq.cloudatlas.gossiping.dataformat.EncodedGossip;
import stupaq.cloudatlas.messaging.messages.gossips.Gossip;
import stupaq.cloudatlas.messaging.messages.gossips.OutboundGossip;
import stupaq.compact.CompactInput;
import stupaq.compact.CompactOutput;
import stupaq.compact.TypeRegistry;

/** PACKAGE-LOCAL */
class GossipCodec extends MessageToMessageCodec<EncodedGossip, OutboundGossip> {
  @Override
  protected void encode(ChannelHandlerContext ctx, OutboundGossip msg, List<Object> out)
      throws IOException {
    ByteBuf buffer = Unpooled.buffer();
    try {
      CompactOutput stream = new CompactOutput(new ByteBufOutputStream(buffer));
      TypeRegistry.writeObject(stream, msg.gossip());
      out.add(new EncodedGossip(msg.recipient(), buffer));
    } finally {
      buffer.release();
    }
  }

  @Override
  protected void decode(ChannelHandlerContext ctx, EncodedGossip msg, List<Object> out)
      throws IOException {
    CompactInput stream = new CompactInput(msg.dataStream());
    Gossip gossip = TypeRegistry.readObject(stream);
    if (!gossip.hasSender()) {
      gossip.sender(msg.contact());
    }
    out.add(gossip);
  }
}
