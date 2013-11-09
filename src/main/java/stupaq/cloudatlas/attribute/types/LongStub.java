package stupaq.cloudatlas.attribute.types;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import stupaq.cloudatlas.attribute.AttributeValue;
import stupaq.cloudatlas.serialization.SerializationOnly;

/** PACKAGE-LOCAL */
abstract class LongStub extends PrimitiveWrapper<Long> implements AttributeValue {
  @SerializationOnly
  public LongStub() {
    this(0L);
  }

  public LongStub(Long value) {
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
}