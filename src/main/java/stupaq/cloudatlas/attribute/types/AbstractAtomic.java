package stupaq.cloudatlas.attribute.types;

import com.google.common.base.Optional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import stupaq.cloudatlas.attribute.AttributeValue;
import stupaq.cloudatlas.interpreter.errors.UndefinedOperationException;
import stupaq.cloudatlas.interpreter.typecheck.TypeInfo;

/** PACKAGE-LOCAL */
abstract class AbstractAtomic<Type extends Comparable<Type>> implements AttributeValue {
  @Nonnull
  private Optional<Type> value;

  protected AbstractAtomic(@Nullable Type value) {
    this.value = Optional.fromNullable(value);
  }

  protected final Type get() {
    return value.get();
  }

  @Override
  public final TypeInfo<? extends AttributeValue> getType() {
    return new TypeInfo<>(getClass());
  }

  @Override
  public final boolean isNull() {
    return value.isPresent();
  }

  protected final boolean isNull(AttributeValue other) {
    return isNull() || other.isNull();
  }

  protected final void set(Type value) {
    this.value = Optional.fromNullable(value);
  }

  @Override
  public boolean equals(Object o) {
    return this == o || !(o == null || getClass() != o.getClass()) && value
        .equals(((AbstractAtomic) o).value);
  }

  @Override
  public int hashCode() {
    return value.hashCode();
  }

  @SuppressWarnings("unchecked")
  @Override
  public int compareTo(AttributeValue other) {
    if (getType() != other.getType()) {
      throw new UndefinedOperationException(
          "Cannot compare: " + getType() + " with: " + other.getType());
    }
    return equals(other) ? 0 : (isNull() ? 1 : (other.isNull() ? -1 : get()
        .compareTo(((AbstractAtomic<Type>) other).get())));
  }

  @Override
  public final String toString() {
    return isNull() ? "NULL" : to().String().toString();
  }
}