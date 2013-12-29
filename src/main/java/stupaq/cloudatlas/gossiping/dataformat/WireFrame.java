package stupaq.cloudatlas.gossiping.dataformat;

import com.google.common.base.Preconditions;

import java.io.IOException;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.buffer.Unpooled;
import io.netty.channel.socket.DatagramPacket;
import io.netty.util.AbstractReferenceCounted;
import io.netty.util.ReferenceCountUtil;
import stupaq.cloudatlas.attribute.values.CAContact;
import stupaq.cloudatlas.gossiping.GossipingConfigKeys;
import stupaq.compact.CompactInput;
import stupaq.compact.CompactOutput;

import static com.google.common.base.Throwables.propagate;
import static com.google.common.base.Throwables.propagateIfInstanceOf;

public class WireFrame extends AbstractReferenceCounted implements GossipingConfigKeys {
  public static final int HEADERn_MAX_SIZE = FrameId.SERIALIZED_MAX_SIZE;
  public static final int HEADER0_MAX_SIZE = HEADERn_MAX_SIZE + WireGTPHeader.SERIALIZED_MAX_SIZE;
  public static final int DATAn_MAX_SIZE = DATAGRAM_PACKET_MAX_SIZE - HEADERn_MAX_SIZE;
  public static final int DATA0_MAX_SIZE = DATAGRAM_PACKET_MAX_SIZE - HEADERn_MAX_SIZE;
  private final CAContact contact;
  private final FrameId frameId;
  private final ByteBuf header, data;

  public WireFrame(DatagramPacket packet) throws IOException {
    Preconditions.checkNotNull(packet.sender());
    contact = new CAContact(packet.sender());
    ByteBuf content = packet.content();
    CompactInput stream = new CompactInput(new ByteBufInputStream(content));
    frameId = FrameId.SERIALIZER.readInstance(stream);
    // At this point we know that we have the reference
    header = null;
    data = content.retain();
  }

  public WireFrame(CAContact destination, FrameId id, ByteBuf data) throws IOException {
    contact = destination;
    frameId = id;
    this.data = data.retain();
    header = Unpooled.buffer(frameId.isFirst() ? HEADER0_MAX_SIZE : HEADERn_MAX_SIZE);
    try {
      CompactOutput headerStream = new CompactOutput(new ByteBufOutputStream(header));
      FrameId.SERIALIZER.writeInstance(headerStream, frameId);
      // We copy both references when creating a composite
    } catch (Throwable t) {
      this.data.release();
      header.release();
      propagateIfInstanceOf(t, IOException.class);
      throw propagate(t);
    }
  }

  public DatagramPacket packet() {
    Preconditions.checkNotNull(header);
    return new DatagramPacket(Unpooled.wrappedBuffer(header.retain(), data.retain()),
        contact.address());
  }

  public CAContact contact() {
    return contact;
  }

  public ByteBuf data() {
    return data.retain();
  }

  public FrameId frameId() {
    return frameId;
  }

  @Override
  public String toString() {
    return "WireFrame{contact=" + contact + ", frameId=" + frameId + ", header=" + header +
        ", data=" + data + '}';
  }

  @Override
  protected void deallocate() {
    ReferenceCountUtil.release(header);
    ReferenceCountUtil.release(data);
  }

  @Override
  public WireFrame retain() {
    return (WireFrame) super.retain();
  }

  public static int frameDataMaxSize(FrameId frameId) {
    return frameId.isFirst() ? DATA0_MAX_SIZE : DATAn_MAX_SIZE;
  }

  public static int howManyFrames(int dataLength) {
    Preconditions.checkArgument(dataLength >= 0);
    return dataLength == 0 ? 0
        : ((dataLength - DATA0_MAX_SIZE + DATAn_MAX_SIZE - 1) / DATAn_MAX_SIZE + 1);
  }
}
