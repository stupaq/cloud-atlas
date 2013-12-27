package stupaq.cloudatlas.gossiping.dataformat;

import com.google.common.base.Preconditions;

import java.io.IOException;

import javax.annotation.concurrent.Immutable;

import stupaq.commons.base.ForwardingWrapper;
import stupaq.compact.CompactInput;
import stupaq.compact.CompactOutput;
import stupaq.compact.CompactSerializer;

@Immutable
public class GossipId extends ForwardingWrapper<Integer> {
  public static final CompactSerializer<GossipId> SERIALIZER = new CompactSerializer<GossipId>() {
    @Override
    public GossipId readInstance(CompactInput in) throws IOException {
      return new GossipId(in.readInt(), in.readShort());
    }

    @Override
    public void writeInstance(CompactOutput out, GossipId object) throws IOException {
      out.writeInt(object.get());
      out.writeShort(object.framesCount);
    }
  };
  private final short framesCount;

  public GossipId(int value, int framesCount) {
    super(value);
    Preconditions.checkArgument(framesCount <= Short.MAX_VALUE,
        "Too many frames for a single gossip");
    this.framesCount = (short) framesCount;
  }

  public FrameId first() {
    return new FrameId(this, (short) 0);
  }

  public int framesCount() {
    return framesCount;
  }
}
