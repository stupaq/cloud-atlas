package stupaq.cloudatlas.attribute.types;

import stupaq.cloudatlas.attribute.AttributeValue;

public class AttributeTypeTestUtils {
  private AttributeTypeTestUtils() {
  }

  public static CAInteger Int(long value) {
    return new CAInteger(value);
  }

  public static CAInteger Int() {
    return null;
  }

  public static CADuration Dur(long value) {
    return new CADuration(value);
  }

  public static CADuration Dur() {
    return null;
  }

  public static CATime Time(long value) {
    return new CATime(value);
  }

  public static CATime Time() {
    return null;
  }

  public static CADouble Doub(double value) {
    return new CADouble(value);
  }

  public static CADouble Doub() {
    return null;
  }

  public static CABoolean Bool(boolean value) {
    return new CABoolean(value);
  }

  public static CABoolean Bool() {
    return null;
  }

  public static CAString Str(String value) {
    return new CAString(value);
  }

  public static CAString Str() {
    return null;
  }

  public static CAContact Cont(String value) {
    return new CAContact(value);
  }

  public static CAContact Cont() {
    return null;
  }

  public static <Type extends AttributeValue> CAList<Type> List(Type... elems) {
    return new CAList<>(elems);
  }

  public static <Type extends AttributeValue> CAList<Type> ListNull() {
    return null;
  }

  public static <Type extends AttributeValue> CAList<Type> ListEmpty() {
    return new CAList<>();
  }

  public static <Type extends AttributeValue> CASet<Type> Set(Type... elems) {
    return new CASet<>(elems);
  }

  public static <Type extends AttributeValue> CASet<Type> SetNull() {
    return new CASet<>();
  }

  public static <Type extends AttributeValue> CASet<Type> SetEmpty() {
    return new CASet<>();
  }
}
