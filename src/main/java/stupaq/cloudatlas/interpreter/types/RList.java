package stupaq.cloudatlas.interpreter.types;

import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;

import java.util.Iterator;

import stupaq.cloudatlas.interpreter.Value;
import stupaq.cloudatlas.interpreter.errors.EvaluationException;
import stupaq.cloudatlas.interpreter.semantics.BinaryOperation;
import stupaq.cloudatlas.interpreter.semantics.SemanticValue;

public final class RList<Type extends Value> extends RCollection<Type> {
  @SafeVarargs
  public RList(Type... elements) {
    super(elements);
  }

  @Override
  public SemanticValue map(Function<Value, Value> function) {
    return FluentIterable.from(this).transform(function).copyInto(new RList<>());
  }

  @Override
  public SemanticValue zip(SemanticValue second, BinaryOperation<Value, Value, Value> operation) {
    return second.zipWith(this, operation);
  }

  RCollection zipImplementation(Iterator<? extends Value> it1, Iterator<? extends Value> it2,
      BinaryOperation<Value, Value, Value> operation) {
    throw new EvaluationException(
        "Semantic value " + RList.class.getSimpleName() + ", cannot be zipped with other: "
        + SemanticValue.class.getSimpleName());
  }
}
