package stupaq.cloudatlas.attribute.types;

import stupaq.cloudatlas.interpreter.ConvertibleValue;
import stupaq.cloudatlas.interpreter.ConvertibleValue.ConvertibleValueDefault;
import stupaq.cloudatlas.serialization.SerializationOnly;

public class CAInteger extends LongStub {
  @SerializationOnly
  public CAInteger() {
    this(0L);
  }

  public CAInteger(Long value) {
    super(value);
  }

  @Override
  public ConvertibleValue getConvertible() {
    return new ConvertibleImplementation();
  }

  private class ConvertibleImplementation extends ConvertibleValueDefault {
    @Override
    public CADouble to_Double() {
      return new CADouble(getValue().doubleValue());
    }

    @Override
    public CADuration to_Duration() {
      return new CADuration(getValue());
    }

    @Override
    public CAInteger to_Integer() {
      return CAInteger.this;
    }

    @Override
    public CAString to_String() {
      return new CAString(String.valueOf(getValue()));
    }
  }
}
