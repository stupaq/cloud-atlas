package stupaq.cloudatlas.attribute.types;

import org.apache.commons.lang.time.DurationFormatUtils;

import stupaq.cloudatlas.interpreter.ConvertibleValue;
import stupaq.cloudatlas.interpreter.ConvertibleValue.ConvertibleValueDefault;
import stupaq.cloudatlas.serialization.SerializationOnly;

public class CADuration extends LongStub {
  @SerializationOnly
  public CADuration() {
    this(0L);
  }

  public CADuration(Long value) {
    super(value);
  }

  @Override
  public ConvertibleValue getConvertible() {
    return new ConvertibleImplementation();
  }

  @Override
  public Class<CADuration> getType() {
    return CADuration.class;
  }

  private class ConvertibleImplementation extends ConvertibleValueDefault {
    @Override
    public CADuration to_Duration() {
      return CADuration.this;
    }

    @Override
    public CAInteger to_Integer() {
      return new CAInteger(getValue());
    }

    @Override
    public CAString to_String() {
      return new CAString((getValue() >= 0 ? "+" : "-") + DurationFormatUtils
          .formatDuration(Math.abs(getValue()), "d HH:mm:ss.SSS"));
    }
  }
}
