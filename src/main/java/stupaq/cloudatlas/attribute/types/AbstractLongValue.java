package stupaq.cloudatlas.attribute.types;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import stupaq.cloudatlas.attribute.AttributeValue;
import stupaq.cloudatlas.serialization.SerializationOnly;
import stupaq.guava.base.PrimitiveWrapper;

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
  public final Long getValue() {
    return super.getValue();
  }

  @Override
  public final void readFields(ObjectInput in) throws IOException, ClassNotFoundException {
    setValue(in.readLong());
  }

  @Override
  public final void writeFields(ObjectOutput out) throws IOException {
    out.writeLong(getValue());
  }

  @Override
  public final int compareTo(AttributeValue o) {
    TypeUtils.assertSameType(this, o);
    return getValue().compareTo(((AbstractLongValue) o).getValue());
  }

  @Override
  public final String toString() {
    return to().String().toString();
  }
}
