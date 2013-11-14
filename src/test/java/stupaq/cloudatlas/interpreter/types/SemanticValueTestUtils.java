package stupaq.cloudatlas.interpreter.types;

import com.google.common.base.Optional;

import stupaq.cloudatlas.attribute.AttributeValue;
import stupaq.cloudatlas.attribute.types.CABoolean;
import stupaq.cloudatlas.attribute.types.CADouble;
import stupaq.cloudatlas.attribute.types.CAInteger;
import stupaq.cloudatlas.attribute.types.CAList;
import stupaq.cloudatlas.attribute.types.CASet;
import stupaq.guava.base.Function1;
import stupaq.guava.base.Function2;

import static org.junit.Assert.assertEquals;

class SemanticValueTestUtils {
  private SemanticValueTestUtils() {
  }

  /** Generic */
  static <Arg0 extends AttributeValue, Arg1 extends AttributeValue> Function2<Arg0, Arg1, AttributeValue> operation() {
    return new Function2<Arg0, Arg1, AttributeValue>() {
      @Override
      public AttributeValue apply(AttributeValue value1, AttributeValue value2) {
        return value1.op().add(value2.op().negate());
      }
    };
  }

  static <Arg0 extends AttributeValue> Function1<Arg0, AttributeValue> function() {
    return new Function1<Arg0, AttributeValue>() {
      @Override
      public AttributeValue apply(AttributeValue value) {
        return value.op().negate();
      }
    };
  }

  /** Specific */
  static Function1<CAInteger, AttributeValue> UnOp() {
    return function();
  }

  static Function2<CAInteger, CAInteger, AttributeValue> BinOp() {
    return operation();
  }

  static CAInteger Int(long value) {
    return new CAInteger(value);
  }

  static CAInteger Int() {
    return null;
  }

  static CADouble Doub(double value) {
    return new CADouble(value);
  }

  static CADouble Doub() {
    return null;
  }

  static CABoolean Bool(boolean value) {
    return new CABoolean(value);
  }

  static CABoolean Bool() {
    return null;
  }

  static CAList<CAInteger> List(CAInteger... elems) {
    return new CAList<>(elems);
  }

  static CAList<CAInteger> ListNull() {
    return null;
  }

  static CAList<CAInteger> ListEmpty() {
    return new CAList<>();
  }

  static CASet<CAInteger> Set(CAInteger... elems) {
    return new CASet<>(elems);
  }

  static CASet<CAInteger> SetNull() {
    return new CASet<>();
  }

  static CASet<CAInteger> SetEmpty() {
    return new CASet<>();
  }

  static <Type extends AttributeValue> RSingle<Type> S(Type elem) {
    return new RSingle<>(Optional.fromNullable(elem));
  }

  static <Type extends AttributeValue> RList<Type> L(Type... elems) {
    return new RList<>(elems);
  }

  static <Type extends AttributeValue> RCollection<Type> C(Type... elems) {
    return new RCollection<>(elems);
  }

  @SuppressWarnings("unused")
  static void assertEqualsDebug(Object expected, Object actual) {
    assertEquals(expected, actual);
    System.out.println("expected:\t" + String.valueOf(expected) + '\n' +
                       "actual:  \t" + String.valueOf(actual));
  }
}
