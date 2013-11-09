package stupaq.cloudatlas.attribute.types;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import stupaq.cloudatlas.attribute.AttributeValue;
import stupaq.cloudatlas.interpreter.ConvertibleValue;
import stupaq.cloudatlas.interpreter.ConvertibleValue.ConvertibleValueDefault;
import stupaq.cloudatlas.interpreter.Value;
import stupaq.cloudatlas.serialization.SerializationOnly;

// TODO: contact has more structure than string, we don't know it right now though
public class CAContact extends PrimitiveWrapper<String> implements AttributeValue, Value {
  @SerializationOnly
  public CAContact() {
    this(null);
  }

  public CAContact(String value) {
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

  @Override
  public ConvertibleValue getConvertible() {
    return new ConvertibleImplementation();
  }

  @Override
  public Class<CAContact> getType() {
    return CAContact.class;
  }

  private class ConvertibleImplementation extends ConvertibleValueDefault {
    @Override
    public CAContact to_Contact() {
      return CAContact.this;
    }

    @Override
    public CAString to_String() {
      return new CAString(getValue());
    }
  }
}
