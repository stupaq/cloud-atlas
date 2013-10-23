package stupaq.cloudatlas.attribute.types;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import stupaq.cloudatlas.attribute.AttributeValue;
import stupaq.cloudatlas.serialization.SerializationOnly;

public class CABoolean extends PrimitiveWrapper<Boolean> implements AttributeValue {
  @SerializationOnly
  public CABoolean() {
    this(false);
  }

  public CABoolean(Boolean value) {
    super(value);
  }

  @Override
  public void readFields(ObjectInput in) throws IOException, ClassNotFoundException {
    setValue(in.readBoolean());
  }

  @Override
  public void writeFields(ObjectOutput out) throws IOException {
    out.writeBoolean(getValue());
  }
}
