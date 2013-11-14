package stupaq.cloudatlas.interpreter.types;

import stupaq.cloudatlas.attribute.AttributeValue;
import stupaq.cloudatlas.attribute.types.CAInteger;
import stupaq.cloudatlas.interpreter.BinaryOperation;
import stupaq.cloudatlas.interpreter.UnaryOperation;

public class OperationsTestUtils {
  private OperationsTestUtils() {
  }

  static <Arg0 extends AttributeValue, Arg1 extends AttributeValue> BinaryOperation<Arg0, Arg1, AttributeValue> operation() {
    return new BinaryOperation<Arg0, Arg1, AttributeValue>() {
      @Override
      public AttributeValue apply(AttributeValue value1, AttributeValue value2) {
        return value1.op().add(value2.op().negate());
      }
    };
  }

  static BinaryOperation<CAInteger, CAInteger, AttributeValue> integerOp() {
    return operation();
  }

  static <Arg0 extends AttributeValue> UnaryOperation<Arg0, AttributeValue> function() {
    return new UnaryOperation<Arg0, AttributeValue>() {
      @Override
      public AttributeValue apply(AttributeValue value) {
        return value.op().negate();
      }
    };
  }
}
