package stupaq.cloudatlas.attribute.types;

import java.sql.Timestamp;

import stupaq.cloudatlas.interpreter.ConvertibleValue;
import stupaq.cloudatlas.interpreter.ConvertibleValue.ConvertibleValueDefault;
import stupaq.cloudatlas.serialization.SerializationOnly;

public class CATime extends LongStub {
  @SerializationOnly
  public CATime() {
    this(0L);
  }

  public CATime(Long value) {
    super(value);
  }

  public CATime(Timestamp timestamp) {
    this(timestamp.getTime());
  }

  @Override
  public ConvertibleValue getConvertible() {
    return new ConvertibleImplementation();
  }

  private class ConvertibleImplementation extends ConvertibleValueDefault {
    @Override
    public CAString to_String() {
      return new CAString(String.valueOf(new Timestamp(getValue())));
    }

    @Override
    public CATime to_Time() {
      return CATime.this;
    }
  }
}
