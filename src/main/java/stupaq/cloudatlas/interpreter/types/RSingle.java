package stupaq.cloudatlas.interpreter.types;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;

import stupaq.cloudatlas.PrimitiveWrapper;
import stupaq.cloudatlas.interpreter.Value;
import stupaq.cloudatlas.interpreter.semantics.AggregatableValue;
import stupaq.cloudatlas.interpreter.semantics.AggregatableValue.AggregatableValueDefault;
import stupaq.cloudatlas.interpreter.semantics.BinaryOperation;
import stupaq.cloudatlas.interpreter.semantics.SemanticValue;

public final class RSingle<Type extends Value> extends PrimitiveWrapper<Type>
    implements SemanticValue {
  public RSingle(Type value) {
    super(value);
  }

  @Override
  public SemanticValue map(Function<Value, Value> function) {
    return new RSingle<>(function.apply(getValue()));
  }

  @Override
  public SemanticValue zip(SemanticValue second, BinaryOperation<Value, Value, Value> operation) {
    return second.zipWith(this, operation);
  }

  @Override
  @SuppressWarnings("unchecked")
  public <Type extends Value> SemanticValue zipWith(RCollection<Type> first,
      BinaryOperation<Value, Value, Value> operation) {
    return first.zip(first.iterator(), Iterables.cycle(this.getValue()).iterator(), operation);
  }

  @Override
  @SuppressWarnings("unchecked")
  public <Type extends Value> SemanticValue zipWith(RList<Type> first,
      BinaryOperation<Value, Value, Value> operation) {
    return first.zip(first.iterator(), Iterables.cycle(this.getValue()).iterator(), operation);
  }

  @Override
  public <Type extends Value> SemanticValue zipWith(RSingle<Type> first,
      BinaryOperation<Value, Value, Value> operation) {
    return new RSingle<>(operation.apply(first.getValue(), getValue()));
  }

  @Override
  public AggregatableValue aggregate() {
    return new AggregatableValueDefault();
  }
}
