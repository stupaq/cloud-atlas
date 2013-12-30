package stupaq.cloudatlas.services.busybody.sessions;

import java.io.IOException;

import javax.annotation.concurrent.Immutable;

import stupaq.compact.CompactInput;
import stupaq.compact.CompactOutput;
import stupaq.compact.CompactSerializer;

import static stupaq.commons.lang.UnsignedShorts.toInt;

@Immutable
public class SessionId {
  public static final int SERIALIZED_MAX_SIZE = 2;
  public static final CompactSerializer<SessionId> SERIALIZER = new CompactSerializer<SessionId>() {
    @Override
    public SessionId readInstance(CompactInput in) throws IOException {
      return new SessionId(in.readShort());
    }

    @Override
    public void writeInstance(CompactOutput out, SessionId object) throws IOException {
      out.writeShort(object.value);
    }
  };
  private final short value;

  public SessionId() {
    this((short) 0);
  }

  protected SessionId(short value) {
    this.value = value;
  }

  public SessionId nextSession() {
    return new SessionId((short) (intValue() + 1));
  }

  private int intValue() {
    return toInt(value);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    SessionId sessionId = (SessionId) o;
    return value == sessionId.value;
  }

  @Override
  public int hashCode() {
    return (int) value;
  }

  @Override
  public String toString() {
    return "SessionId{value=" + intValue() + '}';
  }
}
