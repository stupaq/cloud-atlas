package stupaq.cloudatlas.attribute.types;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.sql.Timestamp;

import stupaq.cloudatlas.attribute.AttributeValue;
import stupaq.cloudatlas.interpreter.ConvertibleValue;
import stupaq.cloudatlas.interpreter.ConvertibleValue.ConvertibleValueDefault;
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

  @Override
  public ConvertibleValue getConvertible() {
    return new ConvertibleImplementation();
  }

  @Override
  public Class<CAString> getType() {
    return CAString.class;
  }

  private class ConvertibleImplementation extends ConvertibleValueDefault {
    @Override
    public CABoolean to_Boolean() {
      return new CABoolean(Boolean.valueOf(getValue()));
    }

    @Override
    public CAContact to_Contact() {
      return new CAContact(getValue());
    }

    @Override
    public CADouble to_Double() {
      return new CADouble(Double.valueOf(getValue()));
    }

    @Override
    public CADuration to_Duration() {
      return new CADuration(Long.valueOf(getValue()));
    }

    @Override
    public CAInteger to_Integer() {
      return new CAInteger(Long.valueOf(getValue()));
    }

    @Override
    public CAString to_String() {
      return CAString.this;
    }

    @Override
    public CATime to_Time() {
      return new CATime(Timestamp.valueOf(getValue()));
    }
  }
}
