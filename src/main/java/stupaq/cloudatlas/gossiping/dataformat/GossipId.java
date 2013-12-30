package stupaq.cloudatlas.gossiping.dataformat;

import com.google.common.collect.AbstractIterator;
import com.google.common.primitives.UnsignedInteger;

import java.io.IOException;
import java.util.Iterator;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

import stupaq.commons.base.ForwardingWrapper;
import stupaq.compact.CompactInput;
import stupaq.compact.CompactOutput;
import stupaq.compact.CompactSerializer;

import static com.google.common.primitives.UnsignedInteger.ONE;
import static com.google.common.primitives.UnsignedInteger.ZERO;
import static com.google.common.primitives.UnsignedInteger.fromIntBits;

@Immutable
public class GossipId extends ForwardingWrapper<UnsignedInteger> implements Comparable<GossipId> {
  public static final int SERIALIZED_MAX_SIZE = 4;
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

  @Override
  public String toString() {
    return "GossipId{value=" + super.toString() + '}';
  }

  @Override
  public int compareTo(@Nonnull GossipId other) {
    return get().compareTo(other.get());
  }

  public Iterable<FrameId> frames(final int framesCount) {
    return new Iterable<FrameId>() {
      @Override
      public Iterator<FrameId> iterator() {
        return GossipId.this.framesIterator(framesCount);
      }
    };
  }

  public Iterator<FrameId> framesIterator(final int framesCount) {
    return new AbstractIterator<FrameId>() {
      private FrameId id = GossipId.this.firstFrame(framesCount);

      @Override
      protected FrameId computeNext() {
        try {
          return id == null ? endOfData() : id;
        } finally {
          if (id != null) {
            id = id.hasNextFrame() ? id.nextFrame() : null;
          }
        }
      }
    };
  }
}
