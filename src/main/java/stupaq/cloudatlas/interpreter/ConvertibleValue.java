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

  public CABoolean to_Boolean();

  public CAContact to_Contact();

  public CADouble to_Double();

  public CADuration to_Duration();

  public CAInteger to_Integer();

  public CAList to_List();

  public CASet to_Set();

  public CAString to_String();

  public CATime to_Time();

  public CATuple to_Tuple();

  public static abstract class ConvertibleValueDefault implements ConvertibleValue {

    private <T extends Value> T noConversion(Class<T> dest) throws ConversionException {
      throw new ConversionException("Conversion not known to: " + dest.getSimpleName());
    }

    @Override
    public CABoolean to_Boolean() {
      return noConversion(CABoolean.class);
    }

    @Override
    public CAContact to_Contact() {
      return noConversion(CAContact.class);
    }

    @Override
    public CADouble to_Double() {
      return noConversion(CADouble.class);
    }

    @Override
    public CADuration to_Duration() {
      return noConversion(CADuration.class);
    }

    @Override
    public CAInteger to_Integer() {
      return noConversion(CAInteger.class);
    }

    @Override
    public CAList to_List() {
      return noConversion(CAList.class);
    }

    @Override
    public CASet to_Set() {
      return noConversion(CASet.class);
    }

    @Override
    public CAString to_String() {
      return noConversion(CAString.class);
    }

    @Override
    public CATime to_Time() {
      return noConversion(CATime.class);
    }

    @Override
    public CATuple to_Tuple() {
      return noConversion(CATuple.class);
    }
  }

  public static class Stringifier implements Function<Value, String> {
    @Override
    public String apply(@Nullable Value value) {
      return value.getConvertible().to_String().toString();
    }
  }
}
