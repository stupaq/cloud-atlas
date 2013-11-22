package stupaq.cloudatlas.query.semantics;

import com.google.common.base.Function;

import stupaq.cloudatlas.attribute.AttributeValue;
import stupaq.cloudatlas.attribute.values.CABoolean;
import stupaq.cloudatlas.attribute.values.CAContact;
import stupaq.cloudatlas.attribute.values.CADouble;
import stupaq.cloudatlas.attribute.values.CADuration;
import stupaq.cloudatlas.attribute.values.CAInteger;
import stupaq.cloudatlas.attribute.values.CAList;
import stupaq.cloudatlas.attribute.values.CASet;
import stupaq.cloudatlas.attribute.values.CAString;
import stupaq.cloudatlas.attribute.values.CATime;
import stupaq.cloudatlas.query.errors.ConversionException;

public interface ConvertibleValue {

  public CABoolean Boolean();

  public CAContact Contact();

  public CADouble Double();

  public CADuration Duration();

  public CAInteger Integer();

  public CAList List();

  public CASet Set();

  public CAString String();

  public CATime Time();

  public static class ConvertibleValueDefault implements ConvertibleValue {

    private <T extends AttributeValue> T noConversion(Class<T> dest) throws ConversionException {
      throw new ConversionException("Conversion not known to: " + dest.getSimpleName());
    }

    @Override
    public CABoolean Boolean() {
      return noConversion(CABoolean.class);
    }

    @Override
    public CAContact Contact() {
      return noConversion(CAContact.class);
    }

    @Override
    public CADouble Double() {
      return noConversion(CADouble.class);
    }

    @Override
    public CADuration Duration() {
      return noConversion(CADuration.class);
    }

    @Override
    public CAInteger Integer() {
      return noConversion(CAInteger.class);
    }

    @Override
    public CAList List() {
      return noConversion(CAList.class);
    }

    @Override
    public CASet Set() {
      return noConversion(CASet.class);
    }

    @Override
    public CAString String() {
      return noConversion(CAString.class);
    }

    @Override
    public CATime Time() {
      return noConversion(CATime.class);
    }
  }

  public static class Stringifier implements Function<AttributeValue, String> {
    @Override
    public String apply(AttributeValue value) {
      return value.to().String().getString();
    }
  }
}
