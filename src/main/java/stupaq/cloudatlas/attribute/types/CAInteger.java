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
  public ConvertibleValue to() {
    return new ConvertibleImplementation();
  }

  @Override
  public Class<CAInteger> getType() {
    return CAInteger.class;
  }

  private class ConvertibleImplementation extends ConvertibleValueDefault {
    @Override
    public CADouble Double() {
      return new CADouble(getValue().doubleValue());
    }

    @Override
    public CADuration Duration() {
      return new CADuration(getValue());
    }

    @Override
    public CAInteger Integer() {
      return CAInteger.this;
    }

    @Override
    public CAString String() {
      return new CAString(String.valueOf(getValue()));
    }
  }
}
