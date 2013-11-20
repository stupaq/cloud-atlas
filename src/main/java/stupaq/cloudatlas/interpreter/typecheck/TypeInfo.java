package stupaq.cloudatlas.interpreter.typecheck;

import com.google.common.base.Preconditions;

import java.util.Collection;

import javax.annotation.Nonnull;

import stupaq.cloudatlas.attribute.AttributeValue;
import stupaq.cloudatlas.attribute.types.CAInteger;
import stupaq.cloudatlas.interpreter.errors.TypeCheckerException;
import stupaq.guava.base.Function1;
import stupaq.guava.base.Function2;

public class TypeInfo<Atomic extends AttributeValue> {
  @Nonnull
  private final Class<Atomic> type;

  public TypeInfo(@Nonnull Class<Atomic> type) {
    Preconditions.checkNotNull(type);
    this.type = type;
  }

  public Atomic nullInstance() {
    try {
      return type.newInstance();
    } catch (InstantiationException | IllegalAccessException e) {
      throw new IllegalStateException(e);
    }
  }

  public Class<Atomic> get() {
    return type;
  }

  @SuppressWarnings("unchecked")
  public <Result extends AttributeValue> TypeInfo<Result> typeof1(
      Function1<Atomic, Result> function) {
    return (TypeInfo<Result>) function.apply(nullInstance()).getType();
  }

  @SuppressWarnings("unchecked")
  public <Arg1 extends AttributeValue, Result extends AttributeValue> TypeInfo<Result> typeof2(
      TypeInfo<Arg1> other, Function2<Atomic, Arg1, Result> function) {
    return (TypeInfo<Result>) function.apply(nullInstance(), other.nullInstance()).getType();
  }

  @SuppressWarnings("unchecked")
  public TypeInfo<? extends AttributeValue> unfold() {
    throw new TypeCheckerException("Cannot unfold atomic type.");
  }

  public boolean matches(Class<CAInteger> aClass) {
    return type == aClass;
  }

  public <Result extends AttributeValue & Collection<AttributeValue>> Result emptyInstance() {
    throw new TypeCheckerException("Type: " + type.getSimpleName() + " is not an aggregate.");
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
