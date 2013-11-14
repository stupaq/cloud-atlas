package stupaq.cloudatlas.interpreter.types;

import stupaq.cloudatlas.attribute.AttributeValue;
import stupaq.cloudatlas.attribute.types.CAInteger;
import stupaq.guava.base.Function2;
import stupaq.guava.base.Function1;

public class OperationsTestUtils {
  private OperationsTestUtils() {
  }

  static <Arg0 extends AttributeValue, Arg1 extends AttributeValue> Function2<Arg0, Arg1, AttributeValue> operation() {
    return new Function2<Arg0, Arg1, AttributeValue>() {
      @Override
      public AttributeValue apply(AttributeValue value1, AttributeValue value2) {
        return value1.op().add(value2.op().negate());
      }
    };
  }

  static Function2<CAInteger, CAInteger, AttributeValue> integerOp() {
    return operation();
  }

  static <Arg0 extends AttributeValue> Function1<Arg0, AttributeValue> function() {
    return new Function1<Arg0, AttributeValue>() {
      @Override
      public AttributeValue apply(AttributeValue value) {
        return value.op().negate();
      }
    };
  }
}
