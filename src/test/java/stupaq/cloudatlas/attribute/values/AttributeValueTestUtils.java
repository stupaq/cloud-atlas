package stupaq.cloudatlas.attribute.values;

import java.util.Arrays;

import stupaq.cloudatlas.attribute.AttributeValue;
import stupaq.cloudatlas.attribute.types.TypeInfo;

import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;

public class AttributeValueTestUtils {
  private AttributeValueTestUtils() {
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

  @SafeVarargs
  public static <Type extends AttributeValue> CASet<Type> Set(TypeInfo<Type> type, Type... elems) {
    return new CASet<>(type, Arrays.asList(elems));
  }

  @SuppressWarnings("unused")
  public static <Type extends AttributeValue> CASet<Type> SetNull(TypeInfo<Type> type) {
    return new CASet<>(type);
  }

  public static void assertNotEquals(AttributeValue notExpected, AttributeValue actual) {
    assertThat(notExpected, not((AttributeValue) actual));
  }
}
