package stupaq.cloudatlas.gossiping.dataformat;

import com.google.common.collect.AbstractIterator;

import java.io.IOException;
import java.util.Iterator;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

import stupaq.cloudatlas.services.busybody.sessions.SessionId;
import stupaq.compact.CompactInput;
import stupaq.compact.CompactOutput;
import stupaq.compact.CompactSerializer;

import static com.google.common.primitives.UnsignedBytes.toInt;

@Immutable
public class GossipId {
  public static final int SERIALIZED_MAX_SIZE = SessionId.SERIALIZED_MAX_SIZE + 1;
  public static final CompactSerializer<GossipId> SERIALIZER = new CompactSerializer<GossipId>() {
    @Override
    public GossipId readInstance(CompactInput in) throws IOException {
      return new GossipId(SessionId.SERIALIZER.readInstance(in), in.readByte());
    }

    @Override
    public void writeInstance(CompactOutput out, GossipId object) throws IOException {
      SessionId.SERIALIZER.writeInstance(out, object.sessionId);
      out.writeByte(object.value);
    }
  };
  @Nonnull private final SessionId sessionId;
  private final byte value;

  public GossipId(@Nonnull SessionId sessionId) {
    this(sessionId, (byte) 0);
  }

  protected GossipId(@Nonnull SessionId sessionId, byte value) {
    this.sessionId = sessionId;
    this.value = value;
  }

  public GossipId nextGossip() {
    return new GossipId(sessionId, (byte) (toInt(value) + 1));
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    GossipId gossipId = (GossipId) o;
    return value == gossipId.value && sessionId.equals(gossipId.sessionId);
  }

  @Override
  public int hashCode() {
    int result = sessionId.hashCode();
    result = 31 * result + toInt(value);
    return result;
  }

  @Override
  public String toString() {
    return "GossipId{sessionId=" + sessionId + ", value=" + toInt(value) + '}';
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
      private FrameId id = new FrameId(GossipId.this, framesCount);

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
