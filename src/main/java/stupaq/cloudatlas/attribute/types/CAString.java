package stupaq.cloudatlas.attribute.types;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import stupaq.cloudatlas.attribute.AttributeValue;
import stupaq.cloudatlas.interpreter.ConvertibleValue;
import stupaq.cloudatlas.interpreter.ConvertibleValue.ConvertibleValueDefault;
import stupaq.cloudatlas.interpreter.errors.ConversionException;
import stupaq.cloudatlas.serialization.SerializationOnly;

public class CAString extends PrimitiveWrapper<String> implements AttributeValue {
  @SerializationOnly
  public CAString() {
    this(null);
  }

  public CAString(String value) {
    super(value);
  }

  @Override
  public void readFields(ObjectInput in) throws IOException, ClassNotFoundException {
    setValue(in.readUTF());
  }

  @Override
  public void writeFields(ObjectOutput out) throws IOException {
    out.writeUTF(getValue());
  }

  @Override
  public ConvertibleValue to() {
    return new ConvertibleImplementation();
  }

  @Override
  public Class<CAString> getType() {
    return CAString.class;
  }

  private class ConvertibleImplementation extends ConvertibleValueDefault {
    @Override
    public CABoolean Boolean() {
      return new CABoolean(Boolean.valueOf(getValue()));
    }

    @Override
    public CAContact Contact() {
      return new CAContact(getValue());
    }

    @Override
    public CADouble Double() {
      return new CADouble(Double.valueOf(getValue()));
    }

    @Override
    public CADuration Duration() {
      String str = getValue();
      try {
        if (str.charAt(0) != '+' && str.charAt(0) != '-') {
          throw new ConversionException("Expected leading sign");
        }
        String[] parts = str.split(" ");
        long days = Long.parseLong(parts[0]);
        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss.SSS");
        format.setTimeZone(TimeZone.getTimeZone("UTC"));
        long time = format.parse(parts[1]).getTime();
        return new CADuration(
            (days >= 0 ? 1 : -1) * (TimeUnit.DAYS.toMillis(Math.abs(days)) + time));
      } catch (NullPointerException | IndexOutOfBoundsException | NumberFormatException | ParseException e) {
        throw new ConversionException(e);
      }
    }

    @Override
    public CAInteger Integer() {
      return new CAInteger(Long.valueOf(getValue()));
    }

    @Override
    public CAString String() {
      return CAString.this;
    }

    @Override
    public CATime Time() {
      try {
        return new CATime(
            new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS z").parse(getValue()).getTime());
      } catch (ParseException e) {
        throw new ConversionException(e);
      }
    }
  }
}
