package stupaq.cloudatlas.gossiping.dataformat;

import com.google.common.primitives.UnsignedInteger;

import java.io.IOException;

import javax.annotation.concurrent.Immutable;

import stupaq.commons.base.ForwardingWrapper;
import stupaq.compact.CompactInput;
import stupaq.compact.CompactOutput;
import stupaq.compact.CompactSerializer;

import static com.google.common.primitives.UnsignedInteger.ONE;
import static com.google.common.primitives.UnsignedInteger.ZERO;
import static com.google.common.primitives.UnsignedInteger.fromIntBits;

@Immutable
public class GossipId extends ForwardingWrapper<UnsignedInteger> {
  public static final CompactSerializer<GossipId> SERIALIZER = new CompactSerializer<GossipId>() {
    @Override
    public GossipId readInstance(CompactInput in) throws IOException {
      return new GossipId(fromIntBits(in.readInt()));
    }

    @Override
    public void writeInstance(CompactOutput out, GossipId object) throws IOException {
      out.writeInt(object.get().intValue());
    }
  };

  public GossipId() {
    super(ZERO);
  }

  public GossipId(UnsignedInteger value) {
    super(value);
  }

  public GossipId nextGossip() {
    return new GossipId(get().plus(ONE));
  }

  public FrameId firstFrame(int framesCount) {
    return new FrameId(this, framesCount, (short) 0);
  }
}
