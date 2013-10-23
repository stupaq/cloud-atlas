package stupaq.cloudatlas.attribute.types;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import stupaq.cloudatlas.attribute.AttributeValue;
import stupaq.cloudatlas.serialization.SerializationOnly;

public class CAString extends PrimitiveWrapper<String> implements AttributeValue {
  @SerializationOnly
  public CAString() {
    this(null);
  }

  public CAString(String value) {
    super(value);
  }

  @Override
  public void readFields(ObjectInput in) throws IOException, ClassNotFoundException {
    setValue(in.readUTF());
  }

  @Override
  public void writeFields(ObjectOutput out) throws IOException {
    out.writeUTF(getValue());
  }
}
