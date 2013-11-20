package stupaq.cloudatlas.interpreter.values;

import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Iterables;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
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
import stupaq.cloudatlas.interpreter.typecheck.TypeInfo;
import stupaq.cloudatlas.interpreter.typecheck.TypeInfoUtils;
import stupaq.guava.base.Function1;
import stupaq.guava.base.Function2;

import static com.google.common.collect.FluentIterable.from;

abstract class AbstractAggregate<Type extends AttributeValue> extends ArrayList<Type>
    implements SemanticValue<Type> {
  @Nonnull
  protected final TypeInfo<Type> typeInfo;
  private final Supplier<FluentIterable<Type>> nonNulls =
      Suppliers.memoize(new Supplier<FluentIterable<Type>>() {
        @Override
        public FluentIterable<Type> get() {
          return from(AbstractAggregate.this).filter(new Predicate<Type>() {
            @Override
            public boolean apply(Type type) {
              return !type.isNull();
            }
          });
        }
      });
  private final Supplier<Boolean> nullsOnly = Suppliers.memoize(new Supplier<Boolean>() {
    @Override
    public Boolean get() {
      return nonNulls.get().isEmpty() && !AbstractAggregate.this.isEmpty();
    }
  });

  public AbstractAggregate(@Nonnull Iterable<? extends Type> elements,
      @Nonnull TypeInfo<Type> typeInfo) {
    super(new ArrayList<Type>());
    this.typeInfo = typeInfo;
    Preconditions.checkNotNull(typeInfo);
    Preconditions.checkArgument(from(elements).allMatch(Predicates.notNull()));
    Iterables.addAll(this, elements);
  }

  @Override
  public final TypeInfo<Type> getType() {
    return typeInfo;
  }

  @Override
  public final AbstractAggregate<CABoolean> isNull() {
    return from(this).transform(new Function<Type, CABoolean>() {
      @Override
      public CABoolean apply(Type type) {
        return new CABoolean(type.isNull());
      }
    }).copyInto(emptyInstance(TypeInfo.of(CABoolean.class)));
  }

  @Override
  public final <Result extends AttributeValue> SemanticValue<Result> map(
      Function1<Type, Result> function) {
    return from(this).transform(function)
        .copyInto(this.<Result>emptyInstance(TypeInfoUtils.typeof1(getType(), function)));
  }

  protected abstract <Result extends AttributeValue> AbstractAggregate<Result> emptyInstance(
      TypeInfo<Result> typeInfo);

  @Override
  public abstract <Other extends AttributeValue, Result extends AttributeValue> SemanticValue<Result> zip(
      SemanticValue<Other> second, Function2<Type, Other, Result> operation);

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

  /** {@link stupaq.cloudatlas.interpreter.semantics.AggregatingValue} */
  private class AggregatingImplementation extends AggregatingValueDefault<Type> {
    @Override
    public RSingle avg() {
      // Determine whether sub operations are possible
      AttributeValue sum = sum().get();
      CAInteger count = count().get();
      return new RSingle<>(count.isNull() || count.getLong() == 0 ? new CADouble() :
                           sum.op().multiply(count.op().inverse()));
    }

    @Override
    public RSingle sum() {
      // Determine whether we can sum values
      AttributeValue sum = getType().Null().op().zero();
      for (Type elem : nonNulls.get()) {
        sum = sum.op().add(elem);
      }
      return new RSingle<>(nullsOnly.get() ? getType().Null() : sum);
    }

    @Override
    public RSingle<CAInteger> count() {
      return new RSingle<>(new CAInteger(nullsOnly.get() ? null : (long) nonNulls.get().size()));
    }

    @Override
    public RSingle<CAList<Type>> first(final CAInteger size) {
      if (size.isNull() || nullsOnly.get()) {
        return new RSingle<>(new CAList<>(typeInfo));
      }
      return new RSingle<>(new CAList<>(typeInfo, nonNulls.get().limit((int) size.getLong())));
    }

    @Override
    public RSingle<CAList<Type>> last(final CAInteger size) {
      if (size.isNull() || nullsOnly.get()) {
        return new RSingle<>(new CAList<>(typeInfo));
      }
      int toSkip = (int) (nonNulls.get().size() - size.getLong());
      return new RSingle<>(new CAList<>(typeInfo, nonNulls.get().skip(toSkip > 0 ? toSkip : 0)));
    }

    @Override
    public RSingle<CAList<Type>> random(final CAInteger size) {
      if (size.isNull() || nullsOnly.get()) {
        return new RSingle<>(new CAList<>(typeInfo));
      }
      ArrayList<Integer> indices = new ArrayList<>();
      for (int i = 0; i < AbstractAggregate.this.size(); i++) {
        if (!AbstractAggregate.this.get(i).isNull()) {
          indices.add(i);
        }
      }
      Collections.shuffle(indices);
      indices = from(indices).limit((int) size.getLong()).copyInto(new ArrayList<Integer>());
      Collections.sort(indices);
      return new RSingle<>(
          new CAList<>(typeInfo, from(indices).transform(new Function<Integer, Type>() {
            @Override
            public Type apply(Integer integer) {
              return AbstractAggregate.this.get(integer);
            }
          })));
    }

    @Override
    public RSingle<Type> min() {
      // Verify that we can compare
      getType().Null().compareTo(getType().Null());
      return new RSingle<>(
          nonNulls.get().isEmpty() ? getType().Null() : Collections.min(nonNulls.get().toList()));
    }

    @Override
    public RSingle<Type> max() {
      // Verify that we can compare
      getType().Null().compareTo(getType().Null());
      return new RSingle<>(
          nonNulls.get().isEmpty() ? getType().Null() : Collections.max(nonNulls.get().toList()));
    }

    @Override
    public SemanticValue land() {
      // Verify that we can do logical operations
      final AttributeValue neutral = new CABoolean(true).op().and(new CABoolean(true));
      if (nullsOnly.get()) {
        return new RSingle<>(new CABoolean());
      }
      AttributeValue conj = neutral;
      for (Type elem : nonNulls.get()) {
        conj = conj.op().and(elem);
      }
      return new RSingle<>(conj);
    }

    @Override
    public SemanticValue lor() {
      // Verify that we can do logical operations
      final AttributeValue neutral = new CABoolean(false).op().or(new CABoolean(false));
      if (nullsOnly.get()) {
        return new RSingle<>(new CABoolean());
      }
      AttributeValue conj = neutral;
      for (Type elem : nonNulls.get()) {
        conj = conj.op().or(elem);
      }
      return new RSingle<>(conj);
    }

    @Override
    public RList<Type> distinct() {
      final Set<Type> seen = new HashSet<>();
      return nonNulls.get().filter(new Predicate<Type>() {
        @Override
        public boolean apply(Type elem) {
          return !elem.isNull() && seen.add(elem);
        }
      }).copyInto(new RList<>(typeInfo));
    }

    @SuppressWarnings("unchecked")
    @Override
    public SemanticValue unfold() {
      TypeInfo unfolded = typeInfo.unfold();
      if (nullsOnly.get()) {
        return new RSingle(unfolded.Null());
      }
      return nonNulls.get().transformAndConcat(new Function<Type, Iterable<AttributeValue>>() {
        @Override
        public Iterable<AttributeValue> apply(Type elem) {
          try {
            return elem.to().List().asImmutableList();
          } catch (ConversionException e) {
            throw new UndefinedOperationException(
                "Cannot unfold enclosing type: " + elem.getType());
          }
        }
      }).copyInto(new RList<AttributeValue>(unfolded));
    }
  }
}
