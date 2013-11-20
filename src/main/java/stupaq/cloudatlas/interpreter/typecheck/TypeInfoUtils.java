package stupaq.cloudatlas.interpreter.typecheck;

import stupaq.cloudatlas.attribute.AttributeValue;
import stupaq.guava.base.Function1;
import stupaq.guava.base.Function2;

public class TypeInfoUtils {
  private TypeInfoUtils() {
  }

  public static <Atomic extends AttributeValue, Result extends AttributeValue> TypeInfo<Result> typeof1(
      TypeInfo<Atomic> that, Function1<Atomic, Result> function) {
    return (TypeInfo<Result>) function.apply(that.nullInstance()).getType();
  }

  public static <Atomic extends AttributeValue, Arg1 extends AttributeValue, Result extends AttributeValue> TypeInfo<Result> typeof2(
      TypeInfo<Atomic> that, TypeInfo<Arg1> other, Function2<Atomic, Arg1, Result> function) {
    return (TypeInfo<Result>) function.apply(that.nullInstance(), other.nullInstance()).getType();
  }
}
