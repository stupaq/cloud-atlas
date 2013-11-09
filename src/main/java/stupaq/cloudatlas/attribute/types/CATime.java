package stupaq.cloudatlas.attribute.types;

import org.apache.commons.lang.time.DateFormatUtils;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import stupaq.cloudatlas.attribute.AttributeValue;
import stupaq.cloudatlas.interpreter.ConvertibleValue;
import stupaq.cloudatlas.interpreter.ConvertibleValue.ConvertibleValueDefault;
import stupaq.cloudatlas.serialization.SerializationOnly;

public class CATime extends Date implements AttributeValue {
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
  public ConvertibleValue to() {
    return new ConvertibleImplementation();
  }

  @Override
  public void readFields(ObjectInput in) throws IOException, ClassNotFoundException {
    setTime(in.readLong());
  }

  @Override
  public void writeFields(ObjectOutput out) throws IOException {
    out.writeLong(getTime());
  }

  @Override
  public Class<CATime> getType() {
    return CATime.class;
  }

  private class ConvertibleImplementation extends ConvertibleValueDefault {
    @Override
    public CAString String() {
      return new CAString(DateFormatUtils
          .format(getTime(), "yyyy/MM/dd HH:mm:ss.SSS z", TimeZone.getTimeZone("CET")));
    }

    @Override
    public CATime Time() {
      return CATime.this;
    }
  }
}
