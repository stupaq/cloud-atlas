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
    return new SessionId((short) (toInt(value) + 1));
  }

  @Override
  public boolean equals(Object o) {
    return this == o ||
        !(o == null || getClass() != o.getClass()) && value == ((SessionId) o).value;
  }

  @Override
  public int hashCode() {
    return toInt(value);
  }

  @Override
  public String toString() {
    return "SessionId{value=" + toInt(value) + '}';
  }
}
