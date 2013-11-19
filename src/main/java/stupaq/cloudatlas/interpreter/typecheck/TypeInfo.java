package stupaq.cloudatlas.interpreter.typecheck;

import stupaq.cloudatlas.attribute.AttributeValue;
import stupaq.cloudatlas.interpreter.errors.TypeCheckerException;

public class TypeInfo<Atomic extends AttributeValue> {
  private final Class<Atomic> type;

  public TypeInfo(Class<Atomic> type) {
    this.type = type;
  }

  public final Atomic getRepresentative() {
    try {
      return type.newInstance();
    } catch (InstantiationException | IllegalAccessException e) {
      throw new IllegalStateException(e);
    }
  }

  public TypeInfo<? extends AttributeValue> unfold() {
    throw new TypeCheckerException("Cannot unfold atomic type.");
  }
}
