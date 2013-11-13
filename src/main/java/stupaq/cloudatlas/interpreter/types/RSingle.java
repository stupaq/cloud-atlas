package stupaq.cloudatlas.interpreter.types;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;

import stupaq.cloudatlas.PrimitiveWrapper;
import stupaq.cloudatlas.attribute.AttributeValue;
import stupaq.cloudatlas.interpreter.semantics.AggregatingValue;
import stupaq.cloudatlas.interpreter.semantics.AggregatingValue.AggregatingValueDefault;
import stupaq.cloudatlas.interpreter.semantics.BinaryOperation;
import stupaq.cloudatlas.interpreter.semantics.SemanticValue;

/** PACKAGE-LOCAL */
public final class RSingle<Type extends AttributeValue> extends PrimitiveWrapper<Type>
    implements SemanticValue {
  private static final AggregatingValue AGGREGATE_IMPLEMENTATION = new AggregatingValueDefault();

  public RSingle(Type value) {
    super(value);
  }

  @Override
  public SemanticValue map(Function<AttributeValue, AttributeValue> function) {
    return new RSingle<>(function.apply(getValue()));
  }

  @Override
  public SemanticValue zip(SemanticValue second,
      BinaryOperation<AttributeValue, AttributeValue, AttributeValue> operation) {
    return second.zipWith(this, operation);
  }

  @Override
  @SuppressWarnings("unchecked")
  public <Type extends AttributeValue> SemanticValue zipWith(RCollection<Type> first,
      BinaryOperation<AttributeValue, AttributeValue, AttributeValue> operation) {
    return first.zipImplementation(first.iterator(), Iterables.cycle(this.getValue()).iterator(),
        operation);
  }

  @Override
  @SuppressWarnings("unchecked")
  public <Type extends AttributeValue> SemanticValue zipWith(RList<Type> first,
      BinaryOperation<AttributeValue, AttributeValue, AttributeValue> operation) {
    return first.zipImplementation(first.iterator(), Iterables.cycle(this.getValue()).iterator(),
        operation);
  }

  @Override
  public <Type extends AttributeValue> SemanticValue zipWith(RSingle<Type> first,
      BinaryOperation<AttributeValue, AttributeValue, AttributeValue> operation) {
    return new RSingle<>(operation.apply(first.getValue(), getValue()));
  }

  @Override
  public AggregatingValue aggregate() {
    return AGGREGATE_IMPLEMENTATION;
  }
}
