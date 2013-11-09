package stupaq.cloudatlas.interpreter;

import com.google.common.base.Function;

import com.sun.istack.internal.Nullable;

import stupaq.cloudatlas.attribute.types.CABoolean;
import stupaq.cloudatlas.attribute.types.CAContact;
import stupaq.cloudatlas.attribute.types.CADouble;
import stupaq.cloudatlas.attribute.types.CADuration;
import stupaq.cloudatlas.attribute.types.CAInteger;
import stupaq.cloudatlas.attribute.types.CAList;
import stupaq.cloudatlas.attribute.types.CASet;
import stupaq.cloudatlas.attribute.types.CAString;
import stupaq.cloudatlas.attribute.types.CATime;
import stupaq.cloudatlas.attribute.types.CATuple;
import stupaq.cloudatlas.interpreter.errors.ConversionException;

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

  public CATuple Tuple();

  public static abstract class ConvertibleValueDefault implements ConvertibleValue {

    private <T extends Value> T noConversion(Class<T> dest) throws ConversionException {
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

    @Override
    public CATuple Tuple() {
      return noConversion(CATuple.class);
    }
  }

  public static class Stringifier implements Function<Value, String> {
    @Override
    public String apply(@Nullable Value value) {
      return value.to().String().toString();
    }
  }
}
