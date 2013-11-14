package stupaq.cloudatlas.attribute.types;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import stupaq.guava.base.PrimitiveWrapper;
import stupaq.cloudatlas.attribute.AttributeValue;
import stupaq.cloudatlas.serialization.SerializationOnly;

/** PACKAGE-LOCAL */
abstract class AbstractLongValue extends PrimitiveWrapper<Long> implements AttributeValue {
  @SerializationOnly
  public AbstractLongValue() {
    this(0L);
  }

  public AbstractLongValue(Long value) {
    super(value);
  }

  @Override
  public void readFields(ObjectInput in) throws IOException, ClassNotFoundException {
    setValue(in.readLong());
  }

  @Override
  public void writeFields(ObjectOutput out) throws IOException {
    out.writeLong(getValue());
  }

  @Override
  public int compareTo(AttributeValue o) {
    TypeUtils.assertSameType(this, o);
    return getValue().compareTo(((AbstractLongValue) o).getValue());
  }
}
