package stupaq.cloudatlas.attribute.values;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;

import java.io.Serializable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import stupaq.cloudatlas.attribute.AttributeValue;
import stupaq.cloudatlas.query.errors.UndefinedOperationException;
import stupaq.cloudatlas.query.typecheck.TypeInfo;
import stupaq.compact.SerializableImplementation;

/** PACKAGE-LOCAL */
abstract class AbstractAtomic<Type> implements AttributeValue, Serializable {
  private static final long serialVersionUID = 1L;
  @Nonnull private Optional<Type> value;

  @SerializableImplementation
  protected AbstractAtomic() {
    value = Optional.absent();
  }

  protected AbstractAtomic(@Nullable Type value) {
    this.value = Optional.fromNullable(value);
  }

  @Nonnull
  protected final Type get() {
    return value.get();
  }

  @Nullable
  protected final Type orNull() {
    return value.orNull();
  }

  @Override
  public final TypeInfo<? extends AttributeValue> type() {
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
    return this == o ||
        !(o == null || getClass() != o.getClass()) && value.equals(((AbstractAtomic) o).value);
  }

  @Override
  public final int hashCode() {
    return value.hashCode();
  }

  @SuppressWarnings("unchecked")
  @Override
  public int compareTo(AttributeValue other) {
    if (!type().equals(other.type())) {
      throw new UndefinedOperationException("Cannot compare: " + type() + " with: " + other.type());
    } else if (equals(other)) {
      return 0;
    } else if (isNull()) {
      return 1;
    } else if (other.isNull()) {
      return -1;
    } else if (get() instanceof Comparable) {
      return ((Comparable<Type>) get()).compareTo(((AbstractAtomic<Type>) other).get());
    } else {
      throw new UndefinedOperationException("Cannot compare: " + getClass().getSimpleName());
    }
  }

  @Override
  public final String toString() {
    return (isNull() ? "NULL" : to().String().getString()) + type();
  }
}
