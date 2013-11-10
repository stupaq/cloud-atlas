package stupaq.cloudatlas.attribute.types;

import org.apache.commons.lang.time.DateFormatUtils;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import stupaq.cloudatlas.interpreter.Value;
import stupaq.cloudatlas.interpreter.semantics.ConvertibleValue;
import stupaq.cloudatlas.interpreter.semantics.ConvertibleValue.ConvertibleValueDefault;
import stupaq.cloudatlas.interpreter.semantics.OperableValue;
import stupaq.cloudatlas.interpreter.semantics.OperableValue.OperableValueDefault;
import stupaq.cloudatlas.serialization.SerializationOnly;

public class CATime extends LongStub {
  private static final Calendar EPOCH;

  static {
    EPOCH = Calendar.getInstance(TimeZone.getTimeZone("CET"));
    EPOCH.set(2000, Calendar.JANUARY, 1, 0, 0, 0);
    EPOCH.set(Calendar.MILLISECOND, 0);
  }

  @SerializationOnly
  public CATime() {
    this(0L);
  }

  public CATime(long value) {
    super(value);
  }

  /** Returns time corresponding to 2000/01/01 00:00:00.000 CET. */
  public static CATime epoch() {
    return new CATime(EPOCH.getTime().getTime());
  }

  /** Returns current time. */
  public static CATime now() {
    return new CATime(new Date().getTime());
  }

  @Override
  public Class<CATime> getType() {
    return CATime.class;
  }

  @Override
  public ConvertibleValue to() {
    return new ConvertibleImplementation();
  }

  @Override
  public OperableValue operate() {
    return new OperableImplementation();
  }

  private class ConvertibleImplementation extends ConvertibleValueDefault {
    @Override
    public CAString String() {
      return new CAString(DateFormatUtils
          .format(getValue(), "yyyy/MM/dd HH:mm:ss.SSS z", TimeZone.getTimeZone("CET")));
    }

    @Override
    public CATime Time() {
      return CATime.this;
    }
  }

  private class OperableImplementation extends OperableValueDefault {
    @Override
    public Value add(Value value) {
      return value.operate().addTo(CATime.this);
    }

    @Override
    public Value addTo(CADuration value) {
      return new CATime(value.getValue() + getValue());
    }

    @Override
    public Value addTo(CATime value) {
      return new CADuration(value.getValue() + getValue());
    }

    @Override
    public Value negate() {
      return new CATime(-getValue());
    }
  }
}
