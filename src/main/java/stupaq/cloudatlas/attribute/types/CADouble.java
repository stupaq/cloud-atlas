package stupaq.cloudatlas.attribute.types;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import stupaq.cloudatlas.attribute.AttributeValue;
import stupaq.cloudatlas.interpreter.ConvertibleValue;
import stupaq.cloudatlas.interpreter.ConvertibleValue.ConvertibleValueDefault;
import stupaq.cloudatlas.interpreter.Value;
import stupaq.cloudatlas.serialization.SerializationOnly;

public class CADouble extends PrimitiveWrapper<Double> implements AttributeValue, Value {
  @SerializationOnly
  public CADouble() {
    this(0D);
  }

  public CADouble(Double value) {
    super(value);
  }

  @Override
  public void readFields(ObjectInput in) throws IOException, ClassNotFoundException {
    setValue(in.readDouble());
  }

  @Override
  public void writeFields(ObjectOutput out) throws IOException {
    out.writeDouble(getValue());
  }

  @Override
  public ConvertibleValue getConvertible() {
    return new ConvertibleImplementation();
  }

  @Override
  public Class<CADouble> getType() {
    return CADouble.class;
  }

  private class ConvertibleImplementation extends ConvertibleValueDefault {
    @Override
    public CADouble to_Double() {
      return CADouble.this;
    }

    @Override
    public CAInteger to_Integer() {
      return new CAInteger(getValue().longValue());
    }

    @Override
    public CAString to_String() {
      return new CAString(String.valueOf(getValue()));
    }
  }
}
