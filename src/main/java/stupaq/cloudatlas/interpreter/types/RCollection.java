package stupaq.cloudatlas.interpreter.types;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Iterables;

import com.sun.istack.internal.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import stupaq.cloudatlas.attribute.types.CABoolean;
import stupaq.cloudatlas.attribute.types.CAInteger;
import stupaq.cloudatlas.interpreter.Value;
import stupaq.cloudatlas.interpreter.errors.ConversionException;
import stupaq.cloudatlas.interpreter.errors.OperationNotApplicable;
import stupaq.cloudatlas.interpreter.semantics.AggregatableValue;
import stupaq.cloudatlas.interpreter.semantics.AggregatableValue.AggregatableValueDefault;
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

  @Override
  public final AggregatableValue aggregate() {
    return new AggregatableImplementation();
  }

  private class AggregatableImplementation extends AggregatableValueDefault {
    @Override
    public RSingle<Value> avg() {
      // FIXME nulls
      return new RSingle<>(RCollection.this.isEmpty() ? null : sum().getValue().operate()
          .multiply(count().getValue().operate().inverse()));
    }

    @Override
    public RSingle<Value> sum() {
      // FIXME nulls
      Value sum = new CAInteger(0L);
      for (Value elem : RCollection.this) {
        sum = sum.operate().add(elem);
      }
      return new RSingle<>(sum);
    }

    @Override
    public RSingle<CAInteger> count() {
      return new RSingle<>(new CAInteger(RCollection.this.size()));
    }

    @Override
    public RList first(int size) {
      return FluentIterable.from(RCollection.this).limit(size).copyInto(new RList<>());
    }

    @Override
    public RList last(int size) {
      int toSkip = RCollection.this.size() - size;
      return FluentIterable.from(RCollection.this).skip(toSkip > 0 ? toSkip : 0)
          .copyInto(new RList<>());
    }

    @Override
    public RList random(int size) {
      ArrayList<Integer> integers = new ArrayList<>();
      for (int i = 0; i < RCollection.this.size(); i++) {
        integers.add(i);
      }
      Collections.shuffle(integers);
      return FluentIterable.from(integers).limit(size).transform(new Function<Integer, Type>() {
        @Override
        public Type apply(Integer integer) {
          return RCollection.this.get(integer);
        }
      }).copyInto(new RList<Type>());
    }

    @Override
    public SemanticValue min() {
      // FIXME nulls
      return null;
    }

    @Override
    public SemanticValue max() {
      // FIXME nulls
      return null;
    }

    @Override
    public SemanticValue land() {
      // FIXME nulls
      Value res = new CABoolean(true);
      for (Value elem : RCollection.this) {
        res = res.operate().and(elem);
      }
      return new RSingle<>(res);
    }

    @Override
    public SemanticValue lor() {
      // FIXME nulls
      Value res = new CABoolean(false);
      for (Value elem : RCollection.this) {
        res = res.operate().or(elem);
      }
      return new RSingle<>(res);
    }

    @Override
    public RList<Type> distinct() {
      final Set<Type> seen = new HashSet<>();
      return FluentIterable.from(RCollection.this).filter(new Predicate<Type>() {
        @Override
        public boolean apply(@Nullable Type elem) {
          return seen.add(elem);
        }
      }).copyInto(new RList<Type>());
    }

    @Override
    public RList unfold() {
      return FluentIterable.from(RCollection.this)
          .transformAndConcat(new Function<Type, Iterable<Value>>() {
            @Override
            @SuppressWarnings("unchecked")
            public Iterable<Value> apply(@Nullable Type type) {
              try {
                return type.to().List();
              } catch (ConversionException e) {
                throw new OperationNotApplicable("Cannot unfold enclosing type: " + type.getType());
              }
            }
          }).copyInto(new RList<>());
    }
  }
}
