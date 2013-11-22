package stupaq.cloudatlas.attribute.values;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import stupaq.cloudatlas.attribute.AttributeValue;
import stupaq.cloudatlas.query.errors.UndefinedOperationException;
import stupaq.cloudatlas.attribute.types.TypeInfo;

/** PACKAGE-LOCAL */
@ParametersAreNonnullByDefault
abstract class AbstractAtomic<Type extends Comparable<Type>> implements AttributeValue {
  @Nonnull
  private Optional<Type> value;

  protected AbstractAtomic(@Nullable Type value) {
    this.value = Optional.fromNullable(value);
  }

  @Nonnull
  protected final Type get() {
    return value.get();
  }

  @Override
  public final TypeInfo<? extends AttributeValue> getType() {
    return TypeInfo.of(getClass());
  }

  @Override
  public final boolean isNull() {
    return !value.isPresent();
  }

  protected final boolean isNull(AttributeValue other) {
    Preconditions.checkNotNull(other);
    return isNull() || other.isNull();
  }

  protected final void set(Type value) {
    this.value = Optional.fromNullable(value);
  }

  @Override
  public final boolean equals(Object o) {
    return this == o || !(o == null || getClass() != o.getClass()) && value
        .equals(((AbstractAtomic) o).value);
  }

  @Override
  public final int hashCode() {
    return value.hashCode();
  }

  @Override
  public int compareTo(AttributeValue other) {
    if (!getType().equals(other.getType())) {
      throw new UndefinedOperationException(
          "Cannot compare: " + getType() + " with: " + other.getType());
    }
    return equals(other) ? 0 : (isNull() ? 1 : (other.isNull() ? -1 : get()
        .compareTo(((AbstractAtomic<Type>) other).get())));
  }

  @Override
  public final String toString() {
    return (isNull() ? "NULL" : get().toString()) + getType();
  }
}
