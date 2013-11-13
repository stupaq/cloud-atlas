package stupaq.cloudatlas.interpreter.types;

import java.util.Arrays;
import java.util.Iterator;

import stupaq.cloudatlas.interpreter.Value;
import stupaq.cloudatlas.interpreter.errors.EvaluationException;
import stupaq.cloudatlas.interpreter.semantics.BinaryOperation;
import stupaq.cloudatlas.interpreter.semantics.SemanticValue;

public final class RList<Type extends Value> extends AbstractAggregate<Type> {
  @SafeVarargs
  public RList(Type... elements) {
    super(Arrays.asList(elements));
  }

  @Override
  protected <Result extends Value> AbstractAggregate<Result> emptyInstance() {
    return new RList<>();
  }

  @Override
  public SemanticValue zip(SemanticValue second, BinaryOperation<Value, Value, Value> operation) {
    return second.zipWith(this, operation);
  }

  @Override
  RCollection zipImplementation(Iterator<? extends Value> it1, Iterator<? extends Value> it2,
      BinaryOperation<Value, Value, Value> operation) {
    throw new EvaluationException(
        "Semantic value " + RList.class.getSimpleName() + ", cannot be zipped with other: "
        + SemanticValue.class.getSimpleName());
  }
}
