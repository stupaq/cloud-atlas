package stupaq.cloudatlas.interpreter.types;

import com.google.common.base.Optional;

import java.util.Arrays;
import java.util.Iterator;

import stupaq.cloudatlas.attribute.AttributeValue;
import stupaq.cloudatlas.interpreter.errors.EvaluationException;
import stupaq.guava.base.Function2;

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
  public <Other extends AttributeValue, Result extends AttributeValue> SemanticValue<Result> zip(
      SemanticValue<Other> second, Function2<Type, Other, Result> operation) {
    return second.zipWith(this, operation);
  }

  @Override
  <Arg0 extends AttributeValue, Arg1 extends AttributeValue, Result extends AttributeValue> RList<Result> zipImplementation(
      Iterator<Optional<Arg0>> it0, Iterator<Optional<Arg1>> it1,
      Function2<Arg0, Arg1, Result> operation) {
    throw new EvaluationException(
        "Semantic value " + RList.class.getSimpleName() + ", cannot be zipped with other: "
        + SemanticValue.class.getSimpleName());
  }
}
