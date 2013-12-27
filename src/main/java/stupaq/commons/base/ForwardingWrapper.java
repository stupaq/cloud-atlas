package stupaq.commons.base;

import com.google.common.base.Preconditions;

import java.io.Serializable;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

import stupaq.compact.SerializableImplementation;

/**
 * General purpose class that simplifies extending final classes by forwarding {@link
 * #equals(Object)}, {@link #hashCode()} and {@link #toString()}.
 */
@Immutable
public abstract class ForwardingWrapper<Primitive> implements Serializable {
  private static final long serialVersionUID = 1L;
  @Nonnull private final Primitive value;

  @SerializableImplementation
  protected ForwardingWrapper() {
    value = null;
  }

  protected ForwardingWrapper(@Nonnull Primitive value) {
    Preconditions.checkNotNull(value);
    this.value = value;
  }

  @Nonnull
  protected Primitive get() {
    Preconditions.checkNotNull(value);
    return value;
  }

  @Override
  public boolean equals(Object o) {
    return this == o ||
        !(o == null || getClass() != o.getClass()) && get().equals(((ForwardingWrapper) o).get());

  }

  @Override
  public int hashCode() {
    return get().hashCode();
  }

  @Override
  public String toString() {
    return String.valueOf(get());
  }
}
