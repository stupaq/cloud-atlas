package stupaq.cloudatlas.gossiping.dataformat;

import com.google.common.base.Preconditions;

import java.io.IOException;

import javax.annotation.concurrent.Immutable;

import stupaq.compact.CompactInput;
import stupaq.compact.CompactOutput;
import stupaq.compact.CompactSerializer;

@Immutable
public class FrameId {
  public static final CompactSerializer<FrameId> SERIALIZER = new CompactSerializer<FrameId>() {
    @Override
    public FrameId readInstance(CompactInput in) throws IOException {
      return new FrameId(GossipId.SERIALIZER.readInstance(in), in.readShort());
    }

    @Override
    public void writeInstance(CompactOutput out, FrameId object) throws IOException {
      GossipId.SERIALIZER.writeInstance(out, object.gossipId);
      out.writeShort(object.seqNo);
    }
  };
  private final GossipId gossipId;
  private final short seqNo;

  public FrameId(GossipId gossipId, short seqNo) {
    Preconditions.checkNotNull(gossipId);
    this.gossipId = gossipId;
    this.seqNo = seqNo;
  }

  @Override
  public boolean equals(Object o) {
    return this == o ||
        !(o == null || getClass() != o.getClass()) && seqNo == ((FrameId) o).seqNo &&
            gossipId.equals(((FrameId) o).gossipId);
  }

  @Override
  public int hashCode() {
    int result = gossipId.hashCode();
    result = 31 * result + (int) seqNo;
    return result;
  }

  @Override
  public String toString() {
    return "FrameId{" + "gossipId=" + gossipId + ", seqNo=" + seqNo + '}';
  }

  public FrameId next() {
    return new FrameId(gossipId, (short) (seqNo + 1));
  }

  public GossipId gossipId() {
    return gossipId;
  }

  public int seqNo() {
    return seqNo;
  }
}