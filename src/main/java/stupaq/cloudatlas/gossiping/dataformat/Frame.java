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
import stupaq.cloudatlas.time.LocalClock;
import stupaq.compact.CompactInput;
import stupaq.compact.CompactOutput;

import static com.google.common.base.Throwables.propagate;
import static com.google.common.base.Throwables.propagateIfInstanceOf;

public class Frame extends AbstractReferenceCounted implements GossipingConfigKeys {
  private final CAContact contact;
  private final FrameId frameId;
  private final ByteBuf header, data;

  public Frame(DatagramPacket packet, LocalClock clock) throws IOException {
    Preconditions.checkNotNull(packet.sender());
    contact = new CAContact(packet.sender());
    ByteBuf content = packet.content();
    CompactInput stream = new CompactInput(new ByteBufInputStream(content));
    frameId = FrameId.SERIALIZER.readInstance(stream);
    // At this point we know that we have the reference
    header = null;
    data = content.retain();
  }

  public Frame(FrameId id, CAContact destination, ByteBuf data, LocalClock clock)
      throws IOException {
    contact = destination;
    frameId = id;
    this.data = data.retain();
    header = Unpooled.buffer(HEADER_MAX_SIZE);
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
  protected void deallocate() {
    ReferenceCountUtil.release(header);
    ReferenceCountUtil.release(data);
  }

  @Override
  public Frame retain() {
    return (Frame) super.retain();
  }

  public static class FramesBuilder {
    private final LocalClock clock;
    private final CAContact destination;
    private FrameId nextId;

    public FramesBuilder(LocalClock clock, GossipId gossipId, CAContact destination) {
      this.destination = destination;
      nextId = gossipId.first();
      this.clock = clock;
    }

    public Frame next(ByteBuf data) throws IOException {
      FrameId frameId = nextId;
      nextId = nextId.next();
      return new Frame(frameId, destination, data, clock);
    }
  }
}
