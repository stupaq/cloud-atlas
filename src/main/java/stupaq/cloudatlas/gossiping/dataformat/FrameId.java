package stupaq.cloudatlas.gossiping.dataformat;

import com.google.common.base.Preconditions;

import java.io.IOException;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

import stupaq.compact.CompactInput;
import stupaq.compact.CompactOutput;
import stupaq.compact.CompactSerializer;

@Immutable
public class FrameId {
  public static final CompactSerializer<FrameId> SERIALIZER = new CompactSerializer<FrameId>() {
    @Override
    public FrameId readInstance(CompactInput in) throws IOException {
      return new FrameId(GossipId.SERIALIZER.readInstance(in), in.readShort(), in.readShort());
    }

    @Override
    public void writeInstance(CompactOutput out, FrameId object) throws IOException {
      GossipId.SERIALIZER.writeInstance(out, object.gossipId);
      out.writeShort(object.framesCount);
      out.writeShort(object.sequenceNumber);
    }
  };
  @Nonnull private final GossipId gossipId;
  private final short framesCount;
  private final short sequenceNumber;

  public FrameId(@Nonnull GossipId gossipId, long framesCount, long sequenceNumber) {
    Preconditions.checkNotNull(gossipId);
    Preconditions.checkArgument(framesCount <= Short.MAX_VALUE,
        "Too many frames for a single gossip");
    Preconditions.checkState(sequenceNumber < framesCount);
    this.gossipId = gossipId;
    this.framesCount = (short) framesCount;
    this.sequenceNumber = (short) sequenceNumber;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    FrameId frameId = (FrameId) o;
    return framesCount == frameId.framesCount && sequenceNumber == frameId.sequenceNumber &&
        gossipId.equals(frameId.gossipId);
  }

  @Override
  public int hashCode() {
    int result = gossipId.hashCode();
    result = 31 * result + (int) framesCount;
    result = 31 * result + (int) sequenceNumber;
    return result;
  }

  @Override
  public String toString() {
    return "FrameId{" + "gossipId=" + gossipId + ", framesCount=" + framesCount +
        ", sequenceNumber=" + sequenceNumber + '}';
  }

  public FrameId nextFrame() {
    return new FrameId(gossipId, framesCount, (short) (sequenceNumber + 1));
  }

  public boolean hasNextFrame() {
    return sequenceNumber + 1 < framesCount;
  }

  public GossipId gossipId() {
    return gossipId;
  }

  public int framesCount() {
    return framesCount;
  }

  public int sequenceNumber() {
    return sequenceNumber;
  }
}
