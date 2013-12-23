package stupaq.cloudatlas.services.busybody.netty;

import java.io.IOException;
import java.util.List;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.serialization.ObjectDecoderInputStream;
import stupaq.compact.TypeRegistry;

public class CompactSerializableDecoder extends ByteToMessageDecoder {
  @Override
  protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out)
      throws IOException {
    out.add(TypeRegistry.readObject(new ObjectDecoderInputStream(new ByteBufInputStream(in))));
  }
}
