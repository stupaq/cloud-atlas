package stupaq.cloudatlas.gossiping.pipeline;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.util.List;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageCodec;
import stupaq.cloudatlas.gossiping.dataformat.EncodedGossip;
import stupaq.cloudatlas.messaging.messages.Message;
import stupaq.cloudatlas.messaging.messages.gossips.InboundGossip;
import stupaq.cloudatlas.messaging.messages.gossips.OutboundGossip;
import stupaq.compact.CompactSerializable;
import stupaq.compact.TypeRegistry;

/** PACKAGE-LOCAL */
class GossipCodec extends MessageToMessageCodec<EncodedGossip, OutboundGossip> {
  @Override
  protected void encode(ChannelHandlerContext ctx, OutboundGossip msg, List<Object> out)
      throws IOException {
    ByteBuf buffer = Unpooled.buffer();
    try {
      ObjectOutput stream = new ObjectOutputStream(new ByteBufOutputStream(buffer));
      // FIXME
      TypeRegistry.writeObject(stream, (CompactSerializable) msg.getGossip());
      out.add(new EncodedGossip(msg.getContact(), buffer));
    } finally {
      buffer.release();
    }
  }

  @Override
  protected void decode(ChannelHandlerContext ctx, EncodedGossip msg, List<Object> out)
      throws IOException {
    ObjectInput stream = new ObjectInputStream(msg.dataStream());
    out.add(new InboundGossip(msg.contact(), (Message) TypeRegistry.readObject(stream)));
  }
}
