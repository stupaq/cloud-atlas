package stupaq.cloudatlas.interpreter.types;

import java.util.Arrays;
import java.util.Iterator;

import stupaq.cloudatlas.attribute.AttributeValue;
import stupaq.cloudatlas.interpreter.semantics.BinaryOperation;
import stupaq.cloudatlas.interpreter.semantics.SemanticValue;

public class RCollection<Type extends AttributeValue> extends AbstractAggregate<Type> {
  @SafeVarargs
  public RCollection(Type... elements) {
    super(Arrays.asList(elements));
  }

  @Override
  protected <Result extends AttributeValue> AbstractAggregate<Result> emptyInstance() {
    return new RCollection<>();
  }

  @Override
  public SemanticValue zip(SemanticValue second,
      BinaryOperation<AttributeValue, AttributeValue, AttributeValue> operation) {
    return second.zipWith(this, operation);
  }

  @Override
  RCollection zipImplementation(Iterator<? extends AttributeValue> it1,
      Iterator<? extends AttributeValue> it2,
      BinaryOperation<AttributeValue, AttributeValue, AttributeValue> operation) {
    RCollection<AttributeValue> result = new RCollection<>();
    while (it1.hasNext() && it2.hasNext()) {
      result.add(operation.apply(it1.next(), it2.next()));
    }
    return result;
  }
}
