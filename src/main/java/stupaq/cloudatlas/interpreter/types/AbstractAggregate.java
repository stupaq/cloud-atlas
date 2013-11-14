package stupaq.cloudatlas.interpreter.types;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Iterables;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import stupaq.cloudatlas.attribute.AttributeValue;
import stupaq.cloudatlas.attribute.types.CABoolean;
import stupaq.cloudatlas.attribute.types.CADouble;
import stupaq.cloudatlas.attribute.types.CAInteger;
import stupaq.cloudatlas.attribute.types.CAList;
import stupaq.cloudatlas.interpreter.errors.ConversionException;
import stupaq.cloudatlas.interpreter.errors.OperationNotApplicable;
import stupaq.cloudatlas.interpreter.semantics.AggregatingValue;
import stupaq.cloudatlas.interpreter.semantics.AggregatingValue.AggregatingValueDefault;
import stupaq.cloudatlas.interpreter.semantics.SemanticValue;
import stupaq.guava.base.Function1;
import stupaq.guava.base.Function2;
import stupaq.guava.base.Optionals;

abstract class AbstractAggregate<Type extends AttributeValue> extends ArrayList<Optional<Type>>
    implements SemanticValue<Type> {
  public AbstractAggregate(Collection<? extends Type> collection) {
    super(FluentIterable.from(collection).transform(new Function<Type, Optional<Type>>() {
      @Override
      public Optional<Type> apply(Type elem) {
        return Optional.fromNullable(elem);
      }
    }).toList());
  }

  @Override
  public final <Result extends AttributeValue> SemanticValue<Result> map(
      Function1<Type, Result> function) {
    return FluentIterable.from(this).transform(function.liftOptional())
        .copyInto(this.<Result>emptyInstance());
  }

  protected abstract <Result extends AttributeValue> AbstractAggregate<Result> emptyInstance();

  @Override
  public abstract <Other extends AttributeValue, Result extends AttributeValue> SemanticValue<Result> zip(
      SemanticValue<Other> second, Function2<Type, Other, Result> operation);

  @Override
  public final <Other extends AttributeValue, Result extends AttributeValue> SemanticValue<Result> zipWith(
      RCollection<Other> first, Function2<Other, Type, Result> operation) {
    return first.zipImplementation(first.iterator(), this.iterator(), operation);
  }

  @Override
  public final <Other extends AttributeValue, Result extends AttributeValue> SemanticValue<Result> zipWith(
      RList<Other> first, Function2<Other, Type, Result> operation) {
    return first.zipImplementation(first.iterator(), this.iterator(), operation);
  }

  @Override
  @SuppressWarnings("unchecked")
  public final <Other extends AttributeValue, Result extends AttributeValue> SemanticValue<Result> zipWith(
      RSingle<Other> first, Function2<Other, Type, Result> operation) {
    return zipImplementation(Iterables.cycle(first.value).iterator(), iterator(), operation);
  }

  abstract <Arg0 extends AttributeValue, Arg1 extends AttributeValue, Result extends AttributeValue> AbstractAggregate<Result> zipImplementation(
      Iterator<Optional<Arg0>> it0, Iterator<Optional<Arg1>> it1,
      Function2<Arg0, Arg1, Result> operation);

  @Override
  public final boolean equals(Object o) {
    return getClass() == o.getClass() && super.equals(o);
  }

  @Override
  public final AggregatingValue aggregate() {
    return new AggregatingImplementation();
  }

  private Optional<FluentIterable<Type>> presentValues() {
    if (isEmpty()) {
      return Optional.of(FluentIterable.from(Collections.<Type>emptyList()));
    }
    FluentIterable<Type> notNulls = Optionals.presentInstances(this);
    if (notNulls.isEmpty()) {
      return Optional.absent();
    }
    return Optional.of(notNulls);
  }

  private FluentIterable<Optional<Type>> iterable() {
    return FluentIterable.from(this);
  }

  /** {@link stupaq.cloudatlas.interpreter.semantics.AggregatingValue} */
  private class AggregatingImplementation extends AggregatingValueDefault<Type> {
    @Override
    public RSingle avg() {
      return new RSingle<>(AbstractAggregate.this.isEmpty() ? Optional.<CADouble>absent() : Optional
          .of(sum().get().op().multiply(count().get().op().inverse())));
    }

    @Override
    public RSingle sum() {
      return new RSingle<>(
          presentValues().transform(new Function<FluentIterable<Type>, AttributeValue>() {
            @Override
            public AttributeValue apply(FluentIterable<Type> types) {
              AttributeValue sum = new CAInteger(0L);
              for (Type elem : types) {
                sum = sum.op().add(elem);
              }
              return sum;
            }
          }));
    }

    @Override
    public RSingle<CAInteger> count() {
      return new RSingle<>(
          presentValues().transform(new Function<FluentIterable<Type>, CAInteger>() {
            @Override
            public CAInteger apply(FluentIterable<Type> types) {
              return new CAInteger(types.size());
            }
          }));
    }

    @Override
    public RSingle<CAList<Type>> first(final int size) {
      return new RSingle<>(
          presentValues().transform(new Function<FluentIterable<Type>, CAList<Type>>() {
            @Override
            public CAList<Type> apply(FluentIterable<Type> types) {
              return types.limit(size).copyInto(new CAList<Type>());
            }
          }));
    }

    @Override
    public RSingle<CAList<Type>> last(int size) {
      final int toSkip = AbstractAggregate.this.size() - size;
      return new RSingle<>(
          presentValues().transform(new Function<FluentIterable<Type>, CAList<Type>>() {
            @Override
            public CAList<Type> apply(FluentIterable<Type> types) {
              return types.skip(toSkip > 0 ? toSkip : 0).copyInto(new CAList<Type>());
            }
          }));
    }

    @Override
    public RSingle<CAList<Type>> random(final int size) {
      Optional<FluentIterable<Type>> notNulls = presentValues();
      if (!notNulls.isPresent() || notNulls.get().isEmpty()) {
        return new RSingle<>();
      }
      ArrayList<Integer> indices = new ArrayList<>();
      for (int i = 0; i < AbstractAggregate.this.size(); i++) {
        indices.add(i);
      }
      Collections.shuffle(indices);
      return new RSingle<>(
          FluentIterable.from(indices).limit(size).transform(new Function<Integer, Type>() {
            @Override
            public Type apply(Integer integer) {
              return AbstractAggregate.this.get(integer).get();
            }
          }).copyInto(new CAList<Type>()));
    }

    @Override
    public RSingle<Type> min() {
      return new RSingle<>(
          presentValues().transform(new Function<FluentIterable<Type>, Optional<Type>>() {
            @Override
            public Optional<Type> apply(FluentIterable<Type> types) {
              return types.isEmpty() ? Optional.<Type>absent()
                                     : Optional.of(Collections.min(types.toList()));
            }
          }).or(Optional.<Type>absent()));
    }

    @Override
    public RSingle<Type> max() {
      return new RSingle<>(
          presentValues().transform(new Function<FluentIterable<Type>, Optional<Type>>() {
            @Override
            public Optional<Type> apply(FluentIterable<Type> types) {
              return types.isEmpty() ? Optional.<Type>absent()
                                     : Optional.of(Collections.max(types.toList()));
            }
          }).or(Optional.<Type>absent()));
    }

    @Override
    public SemanticValue land() {
      return new RSingle<>(
          presentValues().transform(new Function<FluentIterable<Type>, AttributeValue>() {
            @Override
            public AttributeValue apply(FluentIterable<Type> types) {
              AttributeValue conj = new CABoolean(true);
              for (Type elem : types) {
                conj = conj.op().and(elem);
              }
              return conj;
            }
          }));
    }

    @Override
    public SemanticValue lor() {
      return new RSingle<>(
          presentValues().transform(new Function<FluentIterable<Type>, AttributeValue>() {
            @Override
            public AttributeValue apply(FluentIterable<Type> types) {
              AttributeValue alt = new CABoolean(true);
              for (Type elem : types) {
                alt = alt.op().or(elem);
              }
              return alt;
            }
          }));
    }

    @Override
    public RList<Type> distinct() {
      final Set<Type> seen = new HashSet<>();
      return iterable().filter(new Predicate<Optional<Type>>() {
        @Override
        public boolean apply(Optional<Type> elem) {
          return elem.isPresent() && seen.add(elem.get());
        }
      }).copyInto(new RList<Type>());
    }

    @Override
    public SemanticValue unfold() {
      Optional<FluentIterable<Type>> notNulls = presentValues();
      return !notNulls.isPresent() ? new RSingle() : notNulls.get()
          .transformAndConcat(new Function<Type, Iterable<AttributeValue>>() {
            @Override
            public Iterable<AttributeValue> apply(Type elem) {
              try {
                return elem.to().List();
              } catch (ConversionException e) {
                throw new OperationNotApplicable("Cannot unfold enclosing type: " + elem.getType());
              }
            }
          }).transform(Optionals.<AttributeValue>optionalOf()).copyInto(new RList<>());
    }
  }
}
