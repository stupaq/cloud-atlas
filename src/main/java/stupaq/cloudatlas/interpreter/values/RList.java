package stupaq.cloudatlas.interpreter.values;

import java.util.Collections;
import java.util.Iterator;

import javax.annotation.Nonnull;

import stupaq.cloudatlas.attribute.AttributeValue;
import stupaq.cloudatlas.interpreter.errors.EvaluationException;
import stupaq.cloudatlas.interpreter.typecheck.TypeInfo;
import stupaq.guava.base.Function2;

public final class RList<Type extends AttributeValue> extends AbstractAggregate<Type> {
  public RList(@Nonnull TypeInfo<Type> typeInfo) {
    super(Collections.<Type>emptyList(), typeInfo);
  }

  @Override
  protected <Result extends AttributeValue> AbstractAggregate<Result> emptyInstance(
      TypeInfo<Result> typeInfo) {
    return new RList<>(typeInfo);
  }

  @Override
  public <Other extends AttributeValue, Result extends AttributeValue> SemanticValue<Result> zip(
      SemanticValue<Other> second, Function2<Type, Other, Result> operation) {
    return second.zipWith(this, operation);
  }

  @Override
  <Arg0 extends AttributeValue, Arg1 extends AttributeValue, Result extends AttributeValue> RList<Result> zipImplementation(
      Iterator<Arg0> it0, Iterator<Arg1> it1, Function2<Arg0, Arg1, Result> operation,
      TypeInfo<Result> typeInfo1) {
    throw new EvaluationException(
        "Semantic value " + RList.class.getSimpleName() + ", cannot be zipped with other: "
        + SemanticValue.class.getSimpleName());
  }
}
