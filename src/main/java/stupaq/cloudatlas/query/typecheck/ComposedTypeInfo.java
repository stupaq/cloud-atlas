package stupaq.cloudatlas.query.typecheck;

import com.google.common.base.Preconditions;

import java.lang.reflect.InvocationTargetException;

import javax.annotation.Nonnull;

import stupaq.cloudatlas.attribute.AttributeValue;
import stupaq.cloudatlas.attribute.types.CAList;
import stupaq.cloudatlas.attribute.types.CASet;

public class ComposedTypeInfo<Atomic extends AttributeValue> extends TypeInfo<Atomic> {
  @Nonnull
  private TypeInfo<? extends AttributeValue> enclosing;

  private ComposedTypeInfo(Class<Atomic> type,
      @Nonnull TypeInfo<? extends AttributeValue> enclosing) {
    super(type);
    Preconditions.checkNotNull(enclosing);
    this.enclosing = enclosing;
  }

  public static <Atomic extends AttributeValue> ComposedTypeInfo<Atomic> of(Class<Atomic> type,
      TypeInfo<? extends AttributeValue> enclosing) {
    Preconditions.checkArgument(type == CASet.class || type == CAList.class,
        type.getSimpleName() + " is not composed");
    return new ComposedTypeInfo<>(type, enclosing);
  }

  @Override
  public TypeInfo<? extends AttributeValue> unfold() {
    return enclosing;
  }

  @Override
  public Atomic Null() {
    try {
      return type.getDeclaredConstructor(TypeInfo.class).newInstance(enclosing);
    } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
      throw new IllegalStateException(e);
    }
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
