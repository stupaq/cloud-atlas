package stupaq.cloudatlas.attribute.values;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import javax.annotation.concurrent.Immutable;

import stupaq.cloudatlas.attribute.AttributeValue;
import stupaq.cloudatlas.query.errors.ConversionException;
import stupaq.cloudatlas.query.semantics.ConvertibleValue;
import stupaq.cloudatlas.query.semantics.ConvertibleValue.ConvertibleValueDefault;
import stupaq.cloudatlas.query.semantics.OperableValue;
import stupaq.cloudatlas.query.semantics.OperableValue.OperableValueDefault;
import stupaq.cloudatlas.query.semantics.RelationalValue;
import stupaq.cloudatlas.query.semantics.RelationalValue.RelationalValueDefault;
import stupaq.compact.CompactInput;
import stupaq.compact.CompactOutput;
import stupaq.compact.CompactSerializer;
import stupaq.compact.CompactSerializers;
import stupaq.compact.TypeDescriptor;

@Immutable
public final class CAString extends AbstractAtomic<String> {
  public static final CompactSerializer<CAString> SERIALIZER = new CompactSerializer<CAString>() {
    @Override
    public CAString readInstance(CompactInput in) throws IOException {
      return new CAString(CompactSerializers.String.readInstance(in));
    }

    @Override
    public void writeInstance(CompactOutput out, CAString object) throws IOException {
      CompactSerializers.String.writeInstance(out, object.orNull());
    }
  };

  public CAString() {
    super(null);
  }

  public CAString(String value) {
    super(value);
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

  public String getString() {
    return get();
  }

  @Override
  public TypeDescriptor descriptor() {
    return TypeDescriptor.CAString;
  }

  private class ConvertibleImplementation extends ConvertibleValueDefault {
    @Override
    public CABoolean Boolean() {
      return new CABoolean(isNull() ? null : Boolean.valueOf(get()));
    }

    @Override
    public CAContact Contact() {
      return isNull() ? new CAContact() : new CAContact(get());
    }

    @Override
    public CADouble Double() {
      return new CADouble(isNull() ? null : Double.valueOf(get()));
    }

    @Override
    public CADuration Duration() {
      if (isNull()) {
        return new CADuration(null);
      } else {
        String str = get();
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
    }

    @Override
    public CAInteger Integer() {
      return new CAInteger(isNull() ? null : Long.valueOf(get()));
    }

    @Override
    public CAString String() {
      return CAString.this;
    }

    @Override
    public CATime Time() {
      try {
        return new CATime(isNull() ? null :
            new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS z").parse(get()).getTime());
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
      return new CAString(isNull(value) ? null : value.get() + get());
    }

    @Override
    public CABoolean matches(AttributeValue value) {
      return value.op().describes(CAString.this);
    }

    @Override
    public CABoolean describes(CAString value) {
      return new CABoolean(isNull(value) ? null : Pattern.matches(get(), value.get()));
    }

    @Override
    public CAInteger size() {
      return new CAInteger(isNull() ? null : (long) get().length());
    }
  }

  private class RelationalImplementation extends RelationalValueDefault {
    @Override
    public CABoolean lesserThan(AttributeValue value) {
      return value.rel().greaterThan(CAString.this);
    }

    @Override
    public CABoolean greaterThan(CAString value) {
      return new CABoolean(isNull(value) ? null : get().compareTo(value.get()) > 0);
    }

    @Override
    public CABoolean equalsTo(CAString value) {
      return new CABoolean(isNull(value) ? null : get().equals(value.get()));
    }

    @Override
    public CABoolean equalsTo(AttributeValue value) {
      return value.rel().equalsTo(CAString.this);
    }
  }
}
