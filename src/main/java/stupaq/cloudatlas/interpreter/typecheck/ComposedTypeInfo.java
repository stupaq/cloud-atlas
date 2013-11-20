package stupaq.cloudatlas.interpreter.typecheck;

import stupaq.cloudatlas.attribute.AttributeValue;

public class ComposedTypeInfo<Atomic extends AttributeValue> extends TypeInfo<Atomic> {
  private TypeInfo<? extends AttributeValue> enclosing;

  public ComposedTypeInfo(Class<Atomic> type, TypeInfo<? extends AttributeValue> enclosing) {
    super(type);
    this.enclosing = enclosing;
  }

  @Override
  public TypeInfo<? extends AttributeValue> unfold() {
    return enclosing;
  }
}
