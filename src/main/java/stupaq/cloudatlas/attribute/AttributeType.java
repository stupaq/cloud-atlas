package stupaq.cloudatlas.attribute;

import com.google.common.base.Preconditions;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Collection;

import stupaq.cloudatlas.interpreter.Value;

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

  public static void assertUniformCollection(Collection<? extends Value> collection) {
    if (!collection.isEmpty()) {
      Class clazz = collection.iterator().next().getType();
      for (Value elem : collection) {
        Preconditions.checkState(elem.getType() == clazz,
            "Collection contains elements of not matching type");
      }
    }
  }

}
