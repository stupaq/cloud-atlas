package stupaq.cloudatlas.interpreter.typecheck;

import stupaq.cloudatlas.attribute.AttributeValue;
import stupaq.cloudatlas.attribute.types.CAInteger;
import stupaq.cloudatlas.interpreter.errors.TypeCheckerException;
import stupaq.guava.base.Function1;
import stupaq.guava.base.Function2;

public class TypeInfo<Atomic extends AttributeValue> {
  private final Class<Atomic> type;

  public TypeInfo(Class<Atomic> type) {
    this.type = type;
  }

  public Atomic nullInstance() {
    try {
      return type.newInstance();
    } catch (InstantiationException | IllegalAccessException e) {
      throw new IllegalStateException(e);
    }
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

  public Atomic emptyInstance() {
    throw new TypeCheckerException("Type: " + type.getSimpleName() + " is not an aggregate.");
  }
}
