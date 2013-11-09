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
  public ConvertibleValue to() {
    return new ConvertibleImplementation();
  }

  @Override
  public Class<CADuration> getType() {
    return CADuration.class;
  }

  private class ConvertibleImplementation extends ConvertibleValueDefault {
    @Override
    public CADuration Duration() {
      return CADuration.this;
    }

    @Override
    public CAInteger Integer() {
      return new CAInteger(getValue());
    }

    @Override
    public CAString String() {
      return new CAString((getValue() >= 0 ? "+" : "-") + DurationFormatUtils
          .formatDuration(Math.abs(getValue()), "d HH:mm:ss.SSS"));
    }
  }
}
