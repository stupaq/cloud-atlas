package stupaq.cloudatlas.attribute.values;

import org.apache.commons.lang.time.DateFormatUtils;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import javax.annotation.concurrent.Immutable;

import stupaq.cloudatlas.attribute.AttributeValue;
import stupaq.cloudatlas.query.semantics.ConvertibleValue;
import stupaq.cloudatlas.query.semantics.ConvertibleValue.ConvertibleValueDefault;
import stupaq.cloudatlas.query.semantics.OperableValue;
import stupaq.cloudatlas.query.semantics.OperableValue.OperableValueDefault;
import stupaq.cloudatlas.query.semantics.RelationalValue;
import stupaq.cloudatlas.query.semantics.RelationalValue.RelationalValueDefault;
import stupaq.compact.CompactSerializer;
import stupaq.compact.CompactSerializers;
import stupaq.compact.TypeDescriptor;

@Immutable
public final class CATime extends AbstractAtomic<Long> {
  public static final CompactSerializer<CATime> SERIALIZER = new CompactSerializer<CATime>() {
    @Override
    public CATime readInstance(ObjectInput in) throws IOException {
      return new CATime(CompactSerializers.Long.readInstance(in));
    }

    @Override
    public void writeInstance(ObjectOutput out, CATime object) throws IOException {
      CompactSerializers.Long.writeInstance(out, object.orNull());
    }
  };
  private static final Calendar EPOCH;

  static {
    TimeZone.setDefault(TimeZone.getTimeZone("CET"));
    EPOCH = Calendar.getInstance(TimeZone.getDefault());
    EPOCH.set(2000, Calendar.JANUARY, 1, 0, 0, 0);
    EPOCH.set(Calendar.MILLISECOND, 0);
  }

  public CATime() {
    this(null);
  }

  public CATime(Long value) {
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

  @Override
  public TypeDescriptor descriptor() {
    return TypeDescriptor.CATime;
  }

  private class ConvertibleImplementation extends ConvertibleValueDefault {
    @Override
    public CAString String() {
      return new CAString(isNull() ? null :
          DateFormatUtils.format(get(), "yyyy/MM/dd HH:mm:ss.SSS z", TimeZone.getTimeZone("CET")));
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
      return new CATime(isNull(value) ? null : value.get() + get());
    }

    @Override
    public AttributeValue addTo(CATime value) {
      return new CADuration(isNull(value) ? null : value.get() + get());
    }

    @Override
    public AttributeValue negate() {
      return new CATime(isNull() ? null : -get());
    }
  }

  private class RelationalImplementation extends RelationalValueDefault {
    @Override
    public CABoolean lesserThan(AttributeValue value) {
      return value.rel().greaterThan(CATime.this);
    }

    @Override
    public CABoolean greaterThan(CATime value) {
      return new CABoolean(isNull(value) ? null : get().compareTo(value.get()) > 0);
    }

    @Override
    public CABoolean equalsTo(CATime value) {
      return new CABoolean(isNull(value) ? null : get().equals(value.get()));
    }

    @Override
    public CABoolean equalsTo(AttributeValue value) {
      return value.rel().equalsTo(CATime.this);
    }
  }
}
