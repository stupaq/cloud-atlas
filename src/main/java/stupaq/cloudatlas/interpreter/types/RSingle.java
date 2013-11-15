package stupaq.cloudatlas.interpreter.types;

import com.google.common.base.Optional;
import com.google.common.collect.Iterators;

import stupaq.cloudatlas.attribute.AttributeValue;
import stupaq.cloudatlas.attribute.types.CABoolean;
import stupaq.cloudatlas.interpreter.semantics.AggregatingValue;
import stupaq.cloudatlas.interpreter.semantics.AggregatingValue.AggregatingValueDefault;
import stupaq.cloudatlas.interpreter.semantics.SemanticValue;
import stupaq.guava.base.Function1;
import stupaq.guava.base.Function2;

public final class RSingle<Type extends AttributeValue> implements SemanticValue<Type> {
  private static final AggregatingValue AGGREGATE_IMPLEMENTATION = new AggregatingValueDefault();
  /** PACKAGE-LOCAL */
  final Optional<Type> value;

  public RSingle() {
    this.value = Optional.absent();
  }

  public RSingle(Type value) {
    this.value = Optional.of(value);
  }

  public RSingle(Optional<Type> value) {
    this.value = value;
  }

  public Type get() {
    return value.get();
  }

  @Override
  public RSingle<CABoolean> isNull() {
    return new RSingle<>(new CABoolean(!value.isPresent()));
  }

  @Override
  public <Result extends AttributeValue> SemanticValue<Result> map(
      Function1<Type, Result> function) {
    return new RSingle<>(value.transform(function));
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
    return first.zipImplementation(first.iterator(), Iterators.cycle(value), operation);
  }

  @Override
  @SuppressWarnings("unchecked")
  public <Other extends AttributeValue, Result extends AttributeValue> SemanticValue<Result> zipWith(
      RList<Other> first, Function2<Other, Type, Result> operation) {
    return first.zipImplementation(first.iterator(), Iterators.cycle(value), operation);
  }

  @Override
  public <Other extends AttributeValue, Result extends AttributeValue> SemanticValue<Result> zipWith(
      RSingle<Other> first, Function2<Other, Type, Result> operation) {
    return new RSingle<>(operation.applyOptional(first.value, value));
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
