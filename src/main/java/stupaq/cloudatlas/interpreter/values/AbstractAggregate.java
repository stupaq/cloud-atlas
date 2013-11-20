package stupaq.cloudatlas.interpreter.values;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Iterables;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.annotation.Nonnull;

import stupaq.cloudatlas.attribute.AttributeValue;
import stupaq.cloudatlas.attribute.types.CABoolean;
import stupaq.cloudatlas.attribute.types.CADouble;
import stupaq.cloudatlas.attribute.types.CAInteger;
import stupaq.cloudatlas.attribute.types.CAList;
import stupaq.cloudatlas.interpreter.errors.ConversionException;
import stupaq.cloudatlas.interpreter.errors.UndefinedOperationException;
import stupaq.cloudatlas.interpreter.semantics.AggregatingValue;
import stupaq.cloudatlas.interpreter.semantics.AggregatingValue.AggregatingValueDefault;
import stupaq.guava.base.Function1;
import stupaq.guava.base.Function2;

abstract class AbstractAggregate<Type extends AttributeValue> extends ArrayList<Type>
    implements SemanticValue<Type> {
  private final Type nullElement;

  public AbstractAggregate(@Nonnull Iterable<? extends Type> elements, @Nonnull Type nullElement) {
    super(new ArrayList<Type>());
    Preconditions.checkNotNull(nullElement);
    Preconditions.checkArgument(nullElement.isNull());
    Preconditions.checkArgument(FluentIterable.from(elements).allMatch(Predicates.notNull()));
    this.nullElement = nullElement;
    Iterables.addAll(this, elements);
  }

  @Override
  public final AbstractAggregate<CABoolean> isNull() {
    return FluentIterable.from(this).transform(new Function<Type, CABoolean>() {
      @Override
      public CABoolean apply(Type type) {
        return new CABoolean(type.isNull());
      }
    }).copyInto(this.<CABoolean>emptyInstance());
  }

  @Override
  public final <Result extends AttributeValue> SemanticValue<Result> map(
      Function1<Type, Result> function) {
    return FluentIterable.from(this).transform(function).copyInto(this.<Result>emptyInstance());
  }

  protected abstract <Result extends AttributeValue> AbstractAggregate<Result> emptyInstance();

  @Override
  public abstract <Other extends AttributeValue, Result extends AttributeValue>
  SemanticValue<Result> zip(
      SemanticValue<Other> second, Function2<Type, Other, Result> operation);

  @Override
  public final <Other extends AttributeValue, Result extends AttributeValue>
  SemanticValue<Result> zipWith(
      RCollection<Other> first, Function2<Other, Type, Result> operation) {
    return first.zipImplementation(first.iterator(), this.iterator(), operation);
  }

  @Override
  public final <Other extends AttributeValue, Result extends AttributeValue>
  SemanticValue<Result> zipWith(
      RList<Other> first, Function2<Other, Type, Result> operation) {
    return first.zipImplementation(first.iterator(), this.iterator(), operation);
  }

  @Override
  @SuppressWarnings("unchecked")
  public final <Other extends AttributeValue, Result extends AttributeValue>
  SemanticValue<Result> zipWith(
      RSingle<Other> first, Function2<Other, Type, Result> operation) {
    return zipImplementation(Iterables.cycle(first.get()).iterator(), iterator(), operation);
  }

  abstract <Arg0 extends AttributeValue, Arg1 extends AttributeValue,
      Result extends AttributeValue> AbstractAggregate<Result> zipImplementation(
      Iterator<Arg0> it0, Iterator<Arg1> it1, Function2<Arg0, Arg1, Result> operation);

  @Override
  public final RSingle<Type> getSingle() throws SemanticValueCastException {
    throw new SemanticValueCastException();
  }

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
    FluentIterable<Type> notNulls = FluentIterable.from(this).filter(Predicates.notNull());
    return notNulls.isEmpty() ? Optional.<FluentIterable<Type>>absent() : Optional.of(notNulls);
  }

  private FluentIterable<Type> iterable() {
    return FluentIterable.from(this);
  }

  /** {@link stupaq.cloudatlas.interpreter.semantics.AggregatingValue} */
  private class AggregatingImplementation extends AggregatingValueDefault<Type> {
    @Override
    public RSingle avg() {
      return new RSingle<>(
          presentValues().transform(new Function<FluentIterable<Type>, AttributeValue>() {
            @Override
            public AttributeValue apply(FluentIterable<Type> types) {
              return types.isEmpty() ? new CADouble() :
                     sum().get().op().multiply(count().get().op().inverse());
            }
          }).or(new CADouble()));
    }

    @Override
    public RSingle sum() {
      return new RSingle<>(
          presentValues().transform(new Function<FluentIterable<Type>, AttributeValue>() {
            @Override
            public AttributeValue apply(FluentIterable<Type> types) {
              AttributeValue sum =
                  nullElement instanceof CAInteger ? new CAInteger(0) : new CADouble(0);
              for (Type elem : types) {
                sum = sum.op().add(elem);
              }
              return sum;
            }
          }).or(new CAInteger()));
    }

    @Override
    public RSingle<CAInteger> count() {
      return new RSingle<>(
          presentValues().transform(new Function<FluentIterable<Type>, CAInteger>() {
            @Override
            public CAInteger apply(FluentIterable<Type> types) {
              return new CAInteger(types.size());
            }
          }).or(new CAInteger()));
    }

    @Override
    public RSingle<CAList<Type>> first(final int size) {
      return new RSingle<>(
          presentValues().transform(new Function<FluentIterable<Type>, CAList<Type>>() {
            @Override
            public CAList<Type> apply(FluentIterable<Type> types) {
              return new CAList<>(types.limit(size));
            }
          }).or(new CAList<Type>()));
    }

    @Override
    public RSingle<CAList<Type>> last(final int size) {
      return new RSingle<>(
          presentValues().transform(new Function<FluentIterable<Type>, CAList<Type>>() {
            @Override
            public CAList<Type> apply(FluentIterable<Type> types) {
              int toSkip = types.size() - size;
              return new CAList<>(types.skip(toSkip > 0 ? toSkip : 0));
            }
          }).or(new CAList<Type>()));
    }

    @Override
    public RSingle<CAList<Type>> random(final int size) {
      Optional<FluentIterable<Type>> notNulls = presentValues();
      if (!notNulls.isPresent() || notNulls.get().isEmpty()) {
        return new RSingle<>(new CAList<Type>());
      }
      ArrayList<Integer> indices = new ArrayList<>();
      for (int i = 0; i < AbstractAggregate.this.size(); i++) {
        if (!AbstractAggregate.this.get(i).isNull()) {
          indices.add(i);
        }
      }
      Collections.shuffle(indices);
      indices = FluentIterable.from(indices).limit(size).copyInto(new ArrayList<Integer>());
      Collections.sort(indices);
      return new RSingle<>(
          new CAList<>(FluentIterable.from(indices).transform(new Function<Integer, Type>() {
            @Override
            public Type apply(Integer integer) {
              return AbstractAggregate.this.get(integer);
            }
          })));
    }

    @Override
    public RSingle<Type> min() {
      return new RSingle<>(presentValues().transform(new Function<FluentIterable<Type>, Type>() {
        @Override
        public Type apply(FluentIterable<Type> types) {
          return types.isEmpty() ? nullElement : Collections.min(types.toList());
        }
      }).or(nullElement));
    }

    @Override
    public RSingle<Type> max() {
      return new RSingle<>(presentValues().transform(new Function<FluentIterable<Type>, Type>() {
        @Override
        public Type apply(FluentIterable<Type> types) {
          return types.isEmpty() ? nullElement : Collections.max(types.toList());
        }
      }).or(nullElement));
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
          }).or(new CABoolean(true)));
    }

    @Override
    public SemanticValue lor() {
      return new RSingle<>(
          presentValues().transform(new Function<FluentIterable<Type>, AttributeValue>() {
            @Override
            public AttributeValue apply(FluentIterable<Type> types) {
              AttributeValue alt = new CABoolean(false);
              for (Type elem : types) {
                alt = alt.op().or(elem);
              }
              return alt;
            }
          }).or(new CABoolean(false)));
    }

    @Override
    public RList<Type> distinct() {
      final Set<Type> seen = new HashSet<>();
      return iterable().filter(new Predicate<Type>() {
        @Override
        public boolean apply(Type elem) {
          return !elem.isNull() && seen.add(elem);
        }
      }).copyInto(new RList<Type>());
    }

    @Override
    public SemanticValue unfold() {
      Optional<FluentIterable<Type>> notNulls = presentValues();
      // FIXME we have to reach for enclosingType instance here
      return !notNulls.isPresent() ? new RSingle(nullElement) :
             notNulls.get().transformAndConcat(new Function<Type, Iterable<AttributeValue>>() {
               @Override
               public Iterable<AttributeValue> apply(Type elem) {
                 try {
                   return elem.to().List().asImmutableList();
                 } catch (ConversionException e) {
                   throw new UndefinedOperationException(
                       "Cannot unfold enclosing type: " + elem.getType());
                 }
               }
             }).copyInto(new RList<>());
    }
  }
}
