package stupaq.cloudatlas.interpreter.types;

import java.util.Arrays;
import java.util.Iterator;

import stupaq.cloudatlas.interpreter.Value;
import stupaq.cloudatlas.interpreter.semantics.BinaryOperation;
import stupaq.cloudatlas.interpreter.semantics.SemanticValue;

public class RCollection<Type extends Value> extends AbstractAggregate<Type> {
  @SafeVarargs
  public RCollection(Type... elements) {
    super(Arrays.asList(elements));
  }

  @Override
  protected <Result extends Value> AbstractAggregate<Result> emptyInstance() {
    return new RCollection<>();
  }

  @Override
  public SemanticValue zip(SemanticValue second, BinaryOperation<Value, Value, Value> operation) {
    return second.zipWith(this, operation);
  }

  @Override
  RCollection zipImplementation(Iterator<? extends Value> it1, Iterator<? extends Value> it2,
      BinaryOperation<Value, Value, Value> operation) {
    RCollection<Value> result = new RCollection<>();
    while (it1.hasNext() && it2.hasNext()) {
      result.add(operation.apply(it1.next(), it2.next()));
    }
    return result;
  }
}
