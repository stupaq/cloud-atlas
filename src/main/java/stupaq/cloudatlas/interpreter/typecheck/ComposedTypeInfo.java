package stupaq.cloudatlas.interpreter.typecheck;

import com.google.common.base.Preconditions;

import stupaq.cloudatlas.attribute.AttributeValue;

public class ComposedTypeInfo<Atomic extends AttributeValue> extends TypeInfo<Atomic> {
  private TypeInfo<? extends AttributeValue> enclosing;

  public ComposedTypeInfo(Class<Atomic> type, TypeInfo<? extends AttributeValue> enclosing) {
    super(type);
    Preconditions.checkNotNull(enclosing);
    this.enclosing = enclosing;
  }

  @Override
  public TypeInfo<? extends AttributeValue> unfold() {
    return enclosing;
  }

  @Override
  public String toString() {
    return super.toString() + enclosing.toString();
  }

  @Override
  public boolean equals(Object o) {
    return this == o || !(o == null || getClass() != o.getClass()) && super.equals(o) && enclosing
        .equals(((ComposedTypeInfo) o).enclosing);

  }

  @Override
  public int hashCode() {
    int result = super.hashCode();
    result = 31 * result + enclosing.hashCode();
    return result;
  }
}
