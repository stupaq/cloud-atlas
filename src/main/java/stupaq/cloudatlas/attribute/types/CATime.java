package stupaq.cloudatlas.attribute.types;

import org.apache.commons.lang.time.DateFormatUtils;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import stupaq.cloudatlas.attribute.AttributeValue;
import stupaq.cloudatlas.interpreter.semantics.ConvertibleValue;
import stupaq.cloudatlas.interpreter.semantics.ConvertibleValue.ConvertibleValueDefault;
import stupaq.cloudatlas.interpreter.semantics.OperableValue;
import stupaq.cloudatlas.interpreter.semantics.OperableValue.OperableValueDefault;
import stupaq.cloudatlas.interpreter.semantics.RelationalValue;
import stupaq.cloudatlas.interpreter.semantics.RelationalValue.RelationalValueDefault;
import stupaq.cloudatlas.serialization.SerializationOnly;

public class CATime extends AbstractLongValue {
  private static final Calendar EPOCH;

  static {
    TimeZone.setDefault(TimeZone.getTimeZone("CET"));
    EPOCH = Calendar.getInstance(TimeZone.getDefault());
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
  public RelationalValue rel() {
    return new RelationalImplementation();
  }

  @Override
  public ConvertibleValue to() {
    return new ConvertibleImplementation();
  }

  @Override
  public OperableValue op() {
    return new OperableImplementation();
  }

  private class ConvertibleImplementation extends ConvertibleValueDefault {
    @Override
    public CAString String() {
      return new CAString(DateFormatUtils
          .format(get(), "yyyy/MM/dd HH:mm:ss.SSS z", TimeZone.getTimeZone("CET")));
    }

    @Override
    public CATime Time() {
      return CATime.this;
    }
  }

  private class OperableImplementation extends OperableValueDefault {
    @Override
    public AttributeValue add(AttributeValue value) {
      return value.op().addTo(CATime.this);
    }

    @Override
    public AttributeValue addTo(CADuration value) {
      return new CATime(value.get() + get());
    }

    @Override
    public AttributeValue addTo(CATime value) {
      return new CADuration(value.get() + get());
    }

    @Override
    public AttributeValue negate() {
      return new CATime(-get());
    }
  }

  private class RelationalImplementation extends RelationalValueDefault {
    @Override
    public CABoolean lesserThan(AttributeValue value) {
      return value.rel().greaterThan(CATime.this);
    }

    @Override
    public CABoolean greaterThan(CATime value) {
      return new CABoolean(CATime.this.get().compareTo(value.get()) > 0);
    }

    @Override
    public CABoolean equalsTo(CATime value) {
      return new CABoolean(CATime.this.get().equals(value.get()));
    }

    @Override
    public CABoolean equalsTo(AttributeValue value) {
      return value.rel().equalsTo(CATime.this);
    }
  }
}
