package stupaq.cloudatlas.gossiping.dataformat;

import com.google.common.base.Preconditions;

import java.io.IOException;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

import stupaq.compact.CompactInput;
import stupaq.compact.CompactOutput;
import stupaq.compact.CompactSerializer;

import static stupaq.commons.lang.UnsignedShorts.toInt;

@Immutable
public class FrameId {
  public static final int SERIALIZED_MAX_SIZE = GossipId.SERIALIZED_MAX_SIZE + 2 * 2;
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
  private static final int FRAMES_COUNT_MAX = Short.MAX_VALUE; // - Short.MIN_VALUE +1
  @Nonnull private final GossipId gossipId;
  private final short framesCount;
  private final short sequenceNumber;

  public FrameId(@Nonnull GossipId gossipId, int framesCount) {
    this(gossipId, framesCount, 0);
  }

  protected FrameId(@Nonnull GossipId gossipId, int framesCount, int sequenceNumber) {
    Preconditions.checkArgument(framesCount <= FRAMES_COUNT_MAX);
    Preconditions.checkArgument(0 <= sequenceNumber);
    Preconditions.checkArgument(sequenceNumber < framesCount);
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
    result = 31 * result + toInt(framesCount);
    result = 31 * result + toInt(sequenceNumber);
    return result;
  }

  @Override
  public String toString() {
    return "FrameId{" + "gossipId=" + gossipId + ", framesCount=" + toInt(framesCount) +
        ", sequenceNumber=" + toInt(sequenceNumber) + '}';
  }

  public FrameId nextFrame() {
    return new FrameId(gossipId, toInt(framesCount), toInt(sequenceNumber) + 1);
  }

  public boolean hasNextFrame() {
    return toInt(sequenceNumber) + 1 < toInt(framesCount);
  }

  public GossipId gossipId() {
    return gossipId;
  }

  public int framesCount() {
    return toInt(framesCount);
  }

  public int sequenceNumber() {
    return toInt(sequenceNumber);
  }

  public boolean isFirst() {
    return sequenceNumber == 0;
  }
}
