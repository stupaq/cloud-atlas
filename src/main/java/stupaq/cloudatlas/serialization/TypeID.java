package stupaq.cloudatlas.serialization;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public enum TypeID {
  ATTRIBUTE_TYPES_BOOLEAN,
  ATTRIBUTE_TYPES_CONTACT,
  ATTRIBUTE_TYPES_DOUBLE,
  ATTRIBUTE_TYPES_DURATION,
  ATTRIBUTE_TYPES_INTEGER,
  ATTRIBUTE_TYPES_LIST,
  ATTRIBUTE_TYPES_SET,
  ATTRIBUTE_TYPES_STRING,
  ATTRIBUTE_TYPES_TIME,
  ATTRIBUTE_TYPES_TUPLE;

  static {
    assert TypeID.values().length < Byte.MAX_VALUE;
  }

  public static TypeID readInstance(DataInput in) throws IOException {
    return TypeID.values()[in.readByte()];
  }

  public static void writeInstance(DataOutput out, TypeID type) throws IOException {
    out.writeByte(type.ordinal());
  }
}
