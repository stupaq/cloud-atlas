package stupaq.cloudatlas.gossiping.dataformat;

import com.google.common.base.Preconditions;

import java.io.IOException;
import java.net.InetSocketAddress;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.buffer.Unpooled;
import io.netty.channel.socket.DatagramPacket;
import io.netty.util.AbstractReferenceCounted;
import stupaq.cloudatlas.attribute.values.CAContact;
import stupaq.compact.CompactInput;
import stupaq.compact.CompactOutput;

public class Frame extends AbstractReferenceCounted {
  // Static config
  public static final int HEADER_MAX_SIZE = 24;
  public static final int DATAGRAM_MAX_SIZE = 1024;
  public static final int DATA_MAX_SIZE = DATAGRAM_MAX_SIZE - HEADER_MAX_SIZE;
  private final DatagramPacket packet;
  private final FrameId frameId;

  public Frame(DatagramPacket packet) throws IOException {
    Preconditions.checkNotNull(packet.sender());
    this.packet = packet;
    CompactInput stream = new CompactInput(new ByteBufInputStream(packet.content()));
    frameId = FrameId.SERIALIZER.readInstance(stream);
    // At this point we keep the reference
    packet.retain();
  }

  public Frame(FrameId frameId, InetSocketAddress remote, ByteBuf data) throws IOException {
    // FrameId will be encoded in the packet
    this.frameId = null;
    ByteBuf header = Unpooled.buffer(HEADER_MAX_SIZE);
    try {
      FrameId.SERIALIZER.writeInstance(new CompactOutput(new ByteBufOutputStream(header)), frameId);
      // We copy both references when creating a composite,
      // wrapped buffer's reference is inherited by the packet
      packet = new DatagramPacket(Unpooled.wrappedBuffer(header.retain(), data.retain()), remote);
    } finally {
      header.release();
    }
  }

  public DatagramPacket packet() {
    Preconditions.checkState(frameId == null);
    return packet.retain();
  }

  public CAContact sender() {
    Preconditions.checkState(frameId == null);
    return new CAContact(packet.sender());
  }

  public ByteBuf data() {
    Preconditions.checkState(frameId != null);
    return packet.content().retain();
  }

  public FrameId frameId() {
    Preconditions.checkState(frameId != null);
    return frameId;
  }

  @Override
  protected void deallocate() {
    packet.release();
  }

  @Override
  public Frame retain() {
    return (Frame) super.retain();
  }
}
