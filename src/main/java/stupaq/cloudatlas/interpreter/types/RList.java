package stupaq.cloudatlas.interpreter.types;

import java.util.Arrays;
import java.util.Iterator;

import stupaq.cloudatlas.attribute.AttributeValue;
import stupaq.cloudatlas.interpreter.errors.EvaluationException;
import stupaq.cloudatlas.interpreter.semantics.BinaryOperation;
import stupaq.cloudatlas.interpreter.semantics.SemanticValue;

public final class RList<Type extends AttributeValue> extends AbstractAggregate<Type> {
  @SafeVarargs
  public RList(Type... elements) {
    super(Arrays.asList(elements));
  }

  @Override
  protected <Result extends AttributeValue> AbstractAggregate<Result> emptyInstance() {
    return new RList<>();
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
    throw new EvaluationException(
        "Semantic value " + RList.class.getSimpleName() + ", cannot be zipped with other: "
        + SemanticValue.class.getSimpleName());
  }
}
