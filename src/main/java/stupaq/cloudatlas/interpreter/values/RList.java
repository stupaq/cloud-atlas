package stupaq.cloudatlas.interpreter.values;

import com.google.common.collect.Iterables;

import java.util.Collections;

import javax.annotation.Nonnull;

import stupaq.cloudatlas.attribute.AttributeValue;
import stupaq.cloudatlas.interpreter.errors.TypeCheckerException;
import stupaq.cloudatlas.interpreter.typecheck.TypeInfo;
import stupaq.guava.base.Function2;

import static stupaq.cloudatlas.interpreter.typecheck.TypeInfoUtils.typeof2;

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
  public final <Other extends AttributeValue, Result extends AttributeValue> SemanticValue<Result> zipWith(
      RCollection<Other> first, Function2<Other, Type, Result> operation) {
    throw new TypeCheckerException(
        "Semantic value " + RList.class.getSimpleName() + ", cannot be zipped with other: "
        + SemanticValue.class.getSimpleName());
  }

  @Override
  public final <Other extends AttributeValue, Result extends AttributeValue> SemanticValue<Result> zipWith(
      RList<Other> first, Function2<Other, Type, Result> operation) {
    throw new TypeCheckerException(
        "Semantic value " + RList.class.getSimpleName() + ", cannot be zipped with other: "
        + SemanticValue.class.getSimpleName());
  }

  @SuppressWarnings("unchecked")
  @Override
  public final <Other extends AttributeValue, Result extends AttributeValue> SemanticValue<Result> zipWith(
      RSingle<Other> first, Function2<Other, Type, Result> operation) {
    return RCollection
        .zipImplementation(Iterables.<Other>cycle(first.get()).iterator(), iterator(), operation,
            new RList<>(typeof2(first.getType(), getType(), operation)));
  }
}
