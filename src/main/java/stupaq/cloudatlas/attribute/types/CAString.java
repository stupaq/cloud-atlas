package stupaq.cloudatlas.attribute.types;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import stupaq.guava.base.PrimitiveWrapper;
import stupaq.cloudatlas.attribute.AttributeValue;
import stupaq.cloudatlas.interpreter.errors.ConversionException;
import stupaq.cloudatlas.interpreter.semantics.ConvertibleValue;
import stupaq.cloudatlas.interpreter.semantics.ConvertibleValue.ConvertibleValueDefault;
import stupaq.cloudatlas.interpreter.semantics.OperableValue;
import stupaq.cloudatlas.interpreter.semantics.OperableValue.OperableValueDefault;
import stupaq.cloudatlas.interpreter.semantics.RelationalValue;
import stupaq.cloudatlas.interpreter.semantics.RelationalValue.RelationalValueDefault;
import stupaq.cloudatlas.serialization.SerializationOnly;

public class CAString extends PrimitiveWrapper<String> implements AttributeValue {
  @SerializationOnly
  public CAString() {
    super(null, null);
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
  public Class<CAString> getType() {
    return CAString.class;
  }

  @Override
  public RelationalValue rel() {
    return new RelationalImplementation();
  }

  @Override
  public int compareTo(AttributeValue o) {
    TypeUtils.assertSameType(this, o);
    return getValue().compareTo(((CAString) o).getValue());
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
      } catch (NullPointerException | IndexOutOfBoundsException | NumberFormatException |
          ParseException e) {
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

  private class OperableImplementation extends OperableValueDefault {
    @Override
    public AttributeValue add(AttributeValue value) {
      return value.op().addTo(CAString.this);
    }

    @Override
    public AttributeValue addTo(CAString value) {
      return new CAString(value.getValue() + getValue());
    }

    @Override
    public AttributeValue matches(AttributeValue value) {
      return value.op().describes(CAString.this);
    }

    @Override
    public AttributeValue describes(CAString value) {
      return new CABoolean(Pattern.matches(getValue(), value.getValue()));
    }

    @Override
    public AttributeValue size() {
      return new CAInteger((long) CAString.this.getValue().length());
    }
  }

  private class RelationalImplementation extends RelationalValueDefault {
    @Override
    public CABoolean lessThan(AttributeValue value) {
      return value.rel().greaterThan(CAString.this);
    }

    @Override
    public CABoolean greaterThan(CAString value) {
      return new CABoolean(CAString.this.getValue().compareTo(value.getValue()) > 0);
    }

    @Override
    public CABoolean equalsTo(CAString value) {
      return new CABoolean(CAString.this.getValue().equals(value.getValue()));
    }

    @Override
    public CABoolean equalsTo(AttributeValue value) {
      return value.rel().equalsTo(CAString.this);
    }
  }
}
