package stupaq.cloudatlas.interpreter.types;

import com.google.common.base.Optional;

import stupaq.cloudatlas.attribute.AttributeValue;
import stupaq.cloudatlas.attribute.types.CAInteger;
import stupaq.guava.base.Function1;
import stupaq.guava.base.Function2;

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

  public static <Type extends AttributeValue> RSingle<Type> S(Type elem) {
    return new RSingle<>(Optional.fromNullable(elem));
  }

  public static <Type extends AttributeValue> RList<Type> L(Type... elems) {
    return new RList<>(elems);
  }

  public static <Type extends AttributeValue> RCollection<Type> C(Type... elems) {
    return new RCollection<>(elems);
  }
}
