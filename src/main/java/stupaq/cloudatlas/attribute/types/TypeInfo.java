package stupaq.cloudatlas.attribute.types;

import com.google.common.base.Preconditions;

import javax.annotation.Nonnull;

import stupaq.cloudatlas.attribute.AttributeValue;
import stupaq.cloudatlas.attribute.values.CAList;
import stupaq.cloudatlas.attribute.values.CASet;
import stupaq.cloudatlas.query.errors.TypeCheckerException;
import stupaq.guava.base.Function1;
import stupaq.guava.base.Function2;

public class TypeInfo<Atomic extends AttributeValue> {
  @Nonnull
  protected final Class<Atomic> type;

  protected TypeInfo(@Nonnull Class<Atomic> type) {
    Preconditions.checkNotNull(type);
    this.type = type;
  }

  public static <Atomic extends AttributeValue> TypeInfo<Atomic> of(@Nonnull Class<Atomic> type) {
    Preconditions.checkArgument(type != CASet.class && type != CAList.class,
        type.getSimpleName() + " is composed");
    return new TypeInfo<>(type);
  }

  @SuppressWarnings("unchecked")
  public static <Atomic extends AttributeValue, Result extends AttributeValue> TypeInfo<Result> typeof1(
      TypeInfo<Atomic> that, Function1<Atomic, Result> function) {
    return (TypeInfo<Result>) function.apply(that.Null()).getType();
  }

  @SuppressWarnings("unchecked")
  public static <Atomic extends AttributeValue, Arg1 extends AttributeValue, Result extends AttributeValue> TypeInfo<Result> typeof2(
      TypeInfo<Atomic> that, TypeInfo<Arg1> other, Function2<Atomic, Arg1, Result> function) {
    return (TypeInfo<Result>) function.apply(that.Null(), other.Null()).getType();
  }

  public Atomic Null() {
    try {
      return type.newInstance();
    } catch (InstantiationException | IllegalAccessException e) {
      throw new IllegalStateException(e);
    }
  }

  @Deprecated
  public final Class<Atomic> get() {
    return type;
  }

  @SuppressWarnings("unchecked")
  public TypeInfo<? extends AttributeValue> unfold() {
    throw new TypeCheckerException("Cannot unfold atomic type.");
  }

  @Override
  public String toString() {
    // TODO oh God!
    return " : " + type.getSimpleName().replace("CA", "").toLowerCase();
  }

  @Override
  public boolean equals(Object o) {
    return this == o || !(o == null || getClass() != o.getClass()) && type
        .equals(((TypeInfo) o).type);

  }

  @Override
  public int hashCode() {
    return type.hashCode();
  }
}
