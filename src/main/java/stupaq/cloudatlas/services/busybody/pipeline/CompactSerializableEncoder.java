package stupaq.cloudatlas.services.busybody.pipeline;

import java.io.IOException;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import io.netty.handler.codec.serialization.ObjectEncoderOutputStream;
import stupaq.compact.CompactSerializable;
import stupaq.compact.TypeRegistry;

public class CompactSerializableEncoder extends MessageToByteEncoder<CompactSerializable> {
  @Override
  protected void encode(ChannelHandlerContext ctx, CompactSerializable msg, ByteBuf out)
      throws IOException {
    TypeRegistry.writeObject(new ObjectEncoderOutputStream(new ByteBufOutputStream(out)), msg);
  }
}
