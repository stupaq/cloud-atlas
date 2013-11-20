package stupaq.cloudatlas.interpreter.values;

import com.google.common.base.Preconditions;
import com.google.common.collect.Iterators;

import stupaq.cloudatlas.attribute.AttributeValue;
import stupaq.cloudatlas.attribute.types.CABoolean;
import stupaq.cloudatlas.interpreter.semantics.AggregatingValue;
import stupaq.cloudatlas.interpreter.semantics.AggregatingValue.AggregatingValueDefault;
import stupaq.cloudatlas.interpreter.typecheck.TypeInfo;
import stupaq.guava.base.Function1;
import stupaq.guava.base.Function2;

import static stupaq.cloudatlas.interpreter.typecheck.TypeInfoUtils.typeof2;

public final class RSingle<Type extends AttributeValue> implements SemanticValue<Type> {
  private static final AggregatingValue AGGREGATE_IMPLEMENTATION = new AggregatingValueDefault();
  private final Type value;

  public RSingle(Type value) {
    Preconditions.checkNotNull(value);
    this.value = value;
  }

  public Type get() {
    return value;
  }

  @SuppressWarnings("unchecked")
  @Override
  public TypeInfo<Type> getType() {
    return (TypeInfo<Type>) value.getType();
  }

  @Override
  public RSingle<Type> getSingle() {
    return this;
  }

  @Override
  public RSingle<CABoolean> isNull() {
    return new RSingle<>(new CABoolean(value.isNull()));
  }

  @Override
  public <Result extends AttributeValue> SemanticValue<Result> map(
      Function1<Type, Result> function) {
    return new RSingle<>(function.apply(value));
  }

  @Override
  public <Other extends AttributeValue, Result extends AttributeValue> SemanticValue<Result> zip(
      SemanticValue<Other> second, Function2<Type, Other, Result> operation) {
    return second.zipWith(this, operation);
  }

  @Override
  @SuppressWarnings("unchecked")
  public <Other extends AttributeValue, Result extends AttributeValue> SemanticValue<Result> zipWith(
      RCollection<Other> first, Function2<Other, Type, Result> operation) {
    return RCollection.zipImplementation(first.iterator(), Iterators.cycle(value), operation,
        new RCollection<>(typeof2(first.getType(), getType(), operation)));
  }

  @Override
  @SuppressWarnings("unchecked")
  public <Other extends AttributeValue, Result extends AttributeValue> SemanticValue<Result> zipWith(
      RList<Other> first, Function2<Other, Type, Result> operation) {
    return RCollection.zipImplementation(first.iterator(), Iterators.cycle(value), operation,
        new RList<>(typeof2(first.getType(), getType(), operation)));
  }

  @Override
  public <Other extends AttributeValue, Result extends AttributeValue> SemanticValue<Result> zipWith(
      RSingle<Other> first, Function2<Other, Type, Result> operation) {
    return new RSingle<>(operation.apply(first.value, value));
  }

  @Override
  public AggregatingValue aggregate() {
    return AGGREGATE_IMPLEMENTATION;
  }

  @Override
  public boolean equals(Object o) {
    return this == o || !(o == null || getClass() != o.getClass()) && value
        .equals(((RSingle) o).value);

  }

  @Override
  public int hashCode() {
    return value.hashCode();
  }

  @Override
  public String toString() {
    return String.valueOf(value);
  }
}
