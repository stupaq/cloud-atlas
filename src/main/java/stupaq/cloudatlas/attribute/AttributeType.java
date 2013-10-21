package stupaq.cloudatlas.attribute;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

public enum AttributeType {
  BOOLEAN,
  INTEGER,
  DOUBLE,
  STRING,
  TIME,
  DURATION,
  CONTACT,
  TUPLE,
  LIST,
  SET;

  static {
    assert AttributeType.values().length < Byte.MAX_VALUE;
  }

  public static AttributeType readInstance(ObjectInput in) throws IOException {
    return AttributeType.values()[in.readByte()];
  }

  public static void writeInstance(ObjectOutput out, AttributeType type) throws IOException {
    out.writeByte(type.ordinal());
  }
}
