package stupaq.cloudatlas.attribute.types;

import org.apache.commons.lang.time.DateFormatUtils;

import java.sql.Timestamp;
import java.util.Date;
import java.util.TimeZone;

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

  /** Returns time corresponding to 2000/01/01 00:00:00.000 CET. */
  public static CATime epoch() {
    // TODO
    return new CATime();
  }

  /** Returns current time in CET time zone. */
  public static CATime now() {
    // TODO
    return new CATime(new Date().getTime());
  }

  @Override
  public ConvertibleValue getConvertible() {
    return new ConvertibleImplementation();
  }

  @Override
  public Class<CATime> getType() {
    return CATime.class;
  }

  private class ConvertibleImplementation extends ConvertibleValueDefault {
    @Override
    public CAString to_String() {
      return new CAString(DateFormatUtils
          .format(getValue(), "yyyy/MM/dd HH:mm:ss.SSS z", TimeZone.getTimeZone("UTC")));
    }

    @Override
    public CATime to_Time() {
      return CATime.this;
    }
  }
}
