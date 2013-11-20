package stupaq.cloudatlas.interpreter.typecheck;

import com.google.common.base.Preconditions;

import javax.annotation.Nonnull;

import stupaq.cloudatlas.attribute.AttributeValue;
import stupaq.cloudatlas.attribute.types.CAList;
import stupaq.cloudatlas.attribute.types.CASet;
import stupaq.cloudatlas.interpreter.errors.TypeCheckerException;

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
