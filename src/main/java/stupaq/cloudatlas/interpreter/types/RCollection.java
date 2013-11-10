package stupaq.cloudatlas.interpreter.types;

import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Iterables;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

import stupaq.cloudatlas.interpreter.Value;
import stupaq.cloudatlas.interpreter.semantics.BinaryOperation;
import stupaq.cloudatlas.interpreter.semantics.SemanticValue;

public class RCollection<Type extends Value> extends ArrayList<Type> implements SemanticValue {
  @SafeVarargs
  public RCollection(Type... elements) {
    super(Arrays.asList(elements));
  }

  @Override
  public SemanticValue map(Function<Value, Value> function) {
    return FluentIterable.from(this).transform(function).copyInto(new RCollection<>());
  }

  @Override
  public SemanticValue zip(SemanticValue second, BinaryOperation<Value, Value, Value> operation) {
    return second.zipWith(this, operation);
  }

  @Override
  public final <Type extends Value> SemanticValue zipWith(RCollection<Type> first,
      BinaryOperation<Value, Value, Value> operation) {
    return first.zip(first.iterator(), this.iterator(), operation);
  }

  @Override
  public final <Type extends Value> SemanticValue zipWith(RList<Type> first,
      BinaryOperation<Value, Value, Value> operation) {
    return first.zip(first.iterator(), this.iterator(), operation);
  }

  @Override
  @SuppressWarnings("unchecked")
  public final <Type extends Value> SemanticValue zipWith(RSingle<Type> first,
      BinaryOperation<Value, Value, Value> operation) {
    return zip(Iterables.cycle(first.getValue()).iterator(), this.iterator(), operation);
  }

  @Override
  public boolean equals(Object o) {
    return getClass() == o.getClass() && super.equals(o);
  }

  RCollection zip(Iterator<? extends Value> it1, Iterator<? extends Value> it2,
      BinaryOperation<Value, Value, Value> operation) {
    RCollection<Value> result = new RCollection<>();
    while (it1.hasNext() && it2.hasNext()) {
      result.add(operation.apply(it1.next(), it2.next()));
    }
    return result;
  }

}
