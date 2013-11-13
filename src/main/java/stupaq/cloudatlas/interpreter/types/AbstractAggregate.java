package stupaq.cloudatlas.interpreter.types;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Iterables;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import stupaq.cloudatlas.attribute.types.CABoolean;
import stupaq.cloudatlas.attribute.types.CAInteger;
import stupaq.cloudatlas.interpreter.Value;
import stupaq.cloudatlas.interpreter.errors.ConversionException;
import stupaq.cloudatlas.interpreter.errors.OperationNotApplicable;
import stupaq.cloudatlas.interpreter.semantics.AggregatingValue;
import stupaq.cloudatlas.interpreter.semantics.AggregatingValue.AggregatingValueDefault;
import stupaq.cloudatlas.interpreter.semantics.BinaryOperation;
import stupaq.cloudatlas.interpreter.semantics.SemanticValue;

abstract class AbstractAggregate<Type extends Value> extends ArrayList<Type>
    implements SemanticValue {
  public AbstractAggregate(Collection<? extends Type> collection) {
    super(collection);
  }

  @Override
  public final SemanticValue map(Function<Value, Value> function) {
    return FluentIterable.from(this).transform(function).copyInto(emptyInstance());
  }

  protected abstract <Result extends Value> AbstractAggregate<Result> emptyInstance();

  @Override
  public abstract SemanticValue zip(SemanticValue second,
      BinaryOperation<Value, Value, Value> operation);

  @Override
  public final <Type extends Value> SemanticValue zipWith(RCollection<Type> first,
      BinaryOperation<Value, Value, Value> operation) {
    return first.zipImplementation(first.iterator(), this.iterator(), operation);
  }

  @Override
  public final <Type extends Value> SemanticValue zipWith(RList<Type> first,
      BinaryOperation<Value, Value, Value> operation) {
    return first.zipImplementation(first.iterator(), this.iterator(), operation);
  }

  @Override
  @SuppressWarnings("unchecked")
  public final <Type extends Value> SemanticValue zipWith(RSingle<Type> first,
      BinaryOperation<Value, Value, Value> operation) {
    return zipImplementation(Iterables.cycle(first.getValue()).iterator(), this.iterator(),
        operation);
  }

  @Override
  public final boolean equals(Object o) {
    return getClass() == o.getClass() && super.equals(o);
  }

  abstract RCollection zipImplementation(Iterator<? extends Value> it1,
      Iterator<? extends Value> it2, BinaryOperation<Value, Value, Value> operation);

  @Override
  public final AggregatingValue aggregate() {
    return new AggregatingImplementation();
  }

  /** {@link stupaq.cloudatlas.interpreter.semantics.AggregatingValue} */
  private class AggregatingImplementation extends AggregatingValueDefault {
    @Override
    public RSingle<Value> avg() {
      // FIXME nulls
      return new RSingle<>(AbstractAggregate.this.isEmpty() ? null : sum().getValue().op()
          .multiply(count().getValue().op().inverse()));
    }

    @Override
    public RSingle<Value> sum() {
      // FIXME nulls
      Value sum = new CAInteger(0L);
      for (Value elem : AbstractAggregate.this) {
        sum = sum.op().add(elem);
      }
      return new RSingle<>(sum);
    }

    @Override
    public RSingle<CAInteger> count() {
      return new RSingle<>(new CAInteger(AbstractAggregate.this.size()));
    }

    @Override
    public RList first(int size) {
      return FluentIterable.from(AbstractAggregate.this).limit(size).copyInto(new RList<>());
    }

    @Override
    public RList last(int size) {
      int toSkip = AbstractAggregate.this.size() - size;
      return FluentIterable.from(AbstractAggregate.this).skip(toSkip > 0 ? toSkip : 0)
          .copyInto(new RList<>());
    }

    @Override
    public RList random(int size) {
      // FIXME nulls
      ArrayList<Integer> integers = new ArrayList<>();
      for (int i = 0; i < AbstractAggregate.this.size(); i++) {
        integers.add(i);
      }
      Collections.shuffle(integers);
      return FluentIterable.from(integers).limit(size).transform(new Function<Integer, Type>() {
        @Override
        public Type apply(Integer integer) {
          return AbstractAggregate.this.get(integer);
        }
      }).copyInto(new RList<Type>());
    }

    @Override
    public SemanticValue min() {
      // FIXME nulls
      return new RSingle<>(Collections.min(AbstractAggregate.this));
    }

    @Override
    public SemanticValue max() {
      // FIXME nulls
      return new RSingle<>(Collections.max(AbstractAggregate.this));
    }

    @Override
    public SemanticValue land() {
      // FIXME nulls
      Value res = new CABoolean(true);
      for (Value elem : AbstractAggregate.this) {
        res = res.op().and(elem);
      }
      return new RSingle<>(res);
    }

    @Override
    public SemanticValue lor() {
      // FIXME nulls
      Value res = new CABoolean(false);
      for (Value elem : AbstractAggregate.this) {
        res = res.op().or(elem);
      }
      return new RSingle<>(res);
    }

    @Override
    public RList<Type> distinct() {
      final Set<Type> seen = new HashSet<>();
      return FluentIterable.from(AbstractAggregate.this).filter(new Predicate<Type>() {
        @Override
        public boolean apply(Type elem) {
          return seen.add(elem);
        }
      }).copyInto(new RList<Type>());
    }

    @Override
    public RList unfold() {
      return FluentIterable.from(AbstractAggregate.this)
          .transformAndConcat(new Function<Type, Iterable<Value>>() {
            @Override
            @SuppressWarnings("unchecked")
            public Iterable<Value> apply(Type elem) {
              try {
                return elem.to().List();
              } catch (ConversionException e) {
                throw new OperationNotApplicable("Cannot unfold enclosing type: " + elem.getType());
              }
            }
          }).copyInto(new RList<>());
    }
  }
}
