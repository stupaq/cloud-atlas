package stupaq.cloudatlas.query.semantics.values;

import com.google.common.collect.Iterables;

import java.util.Collections;
import java.util.Iterator;

import javax.annotation.Nonnull;

import stupaq.cloudatlas.attribute.AttributeValue;
import stupaq.cloudatlas.query.errors.TypeCheckerException;
import stupaq.cloudatlas.query.typecheck.TypeInfo;
import stupaq.guava.base.Function2;

import static stupaq.cloudatlas.query.typecheck.TypeInfo.typeof2;

public class RColumn<Type extends AttributeValue> extends AbstractAggregate<Type> {
  public RColumn(@Nonnull TypeInfo<Type> typeInfo) {
    super(Collections.<Type>emptyList(), typeInfo);
  }

  static <Arg0 extends AttributeValue, Arg1 extends AttributeValue, Result extends AttributeValue> SemanticValue<Result> zipImplementation(
      Iterator<Arg0> it0, Iterator<Arg1> it1, Function2<Arg0, Arg1, Result> operation,
      AbstractAggregate<Result> result) {
    while (it0.hasNext() && it1.hasNext()) {
      result.add(operation.apply(it0.next(), it1.next()));
    }
    return result;
  }

  @Override
  protected <Result extends AttributeValue> RColumn emptyInstance(
      TypeInfo<Result> typeInfo) {
    return new RColumn(typeInfo);
  }

  @Override
  public <Other extends AttributeValue, Result extends AttributeValue> SemanticValue<Result> zip(
      SemanticValue<Other> second, Function2<Type, Other, Result> operation) {
    return second.zipWith(this, operation);
  }

  @Override
  public final <Other extends AttributeValue, Result extends AttributeValue> SemanticValue<Result> zipWith(
      RColumn first, Function2<Other, Type, Result> operation) {
    return RColumn
        .zipImplementation(first.iterator(), this.iterator(), operation,
            new RColumn(typeof2(first.getType(), getType(), operation)));
  }

  @Override
  public final <Other extends AttributeValue, Result extends AttributeValue> SemanticValue<Result> zipWith(
      RList<Other> first, Function2<Other, Type, Result> operation) {
    throw new TypeCheckerException(
        "Semantic value " + RList.class.getSimpleName() + ", cannot be zipped with other: "
        + SemanticValue.class.getSimpleName());
  }

  @Override
  @SuppressWarnings("unchecked")
  public final <Other extends AttributeValue, Result extends AttributeValue> SemanticValue<Result> zipWith(
      RSingle<Other> first, Function2<Other, Type, Result> operation) {
    return zipImplementation(Iterables.cycle(first.get()).iterator(), iterator(), operation,
        new RColumn(typeof2(first.getType(), getType(), operation)));
  }
}
