package stupaq.cloudatlas.attribute.types;

import java.util.Arrays;
import java.util.Collections;

import stupaq.cloudatlas.attribute.AttributeValue;
import stupaq.cloudatlas.interpreter.typecheck.TypeInfo;

public class AttributeTypeTestUtils {
  private AttributeTypeTestUtils() {
  }

  public static CAInteger Int(long value) {
    return new CAInteger(value);
  }

  public static CAInteger Int() {
    return new CAInteger();
  }

  public static CADuration Dur(long value) {
    return new CADuration(value);
  }

  public static CADuration Dur() {
    return new CADuration();
  }

  public static CATime Time(long value) {
    return new CATime(value);
  }

  public static CATime Time() {
    return new CATime();
  }

  public static CADouble Doub(double value) {
    return new CADouble(value);
  }

  public static CADouble Doub() {
    return new CADouble();
  }

  public static CABoolean Bool(boolean value) {
    return new CABoolean(value);
  }

  public static CABoolean Bool() {
    return new CABoolean();
  }

  public static CAString Str(String value) {
    return new CAString(value);
  }

  public static CAString Str() {
    return new CAString();
  }

  public static CAContact Cont(String value) {
    return new CAContact(value);
  }

  public static CAContact Cont() {
    return new CAContact();
  }

  @SafeVarargs
  public static <Type extends AttributeValue> CAList<Type> List(TypeInfo<Type> type,
      Type... elems) {
    return new CAList<>(type, Arrays.asList(elems));
  }

  public static <Type extends AttributeValue> CAList<Type> ListNull(TypeInfo<Type> type) {
    return new CAList<>(type);
  }

  public static <Type extends AttributeValue> CAList<Type> ListEmpty(TypeInfo<Type> type) {
    return new CAList<>(type, Collections.<Type>emptyList());
  }

  @SafeVarargs
  public static <Type extends AttributeValue> CASet<Type> Set(TypeInfo<Type> type, Type... elems) {
    return new CASet<>(type, Arrays.asList(elems));
  }

  public static <Type extends AttributeValue> CASet<Type> SetEmpty(TypeInfo<Type> type) {
    return new CASet<>(type, Collections.<Type>emptyList());
  }

  @SuppressWarnings("unused")
  public static <Type extends AttributeValue> CASet<Type> SetNull(TypeInfo<Type> type) {
    return new CASet<>(type);
  }
}
