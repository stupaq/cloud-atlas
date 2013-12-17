package stupaq.commons.base;

import com.google.common.base.Preconditions;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

/**
 * General purpose class that simplifies extending final classes by forwarding {@link
 * #equals(Object)}, {@link #hashCode()} and {@link #toString()}.
 */
@Immutable
public abstract class ForwardingWrapper<Primitive> {
  @Nonnull private final Primitive value;

  protected ForwardingWrapper(@Nonnull Primitive value) {
    Preconditions.checkNotNull(value);
    this.value = value;
  }

  @Nonnull
  protected Primitive get() {
    return value;
  }

  @Override
  public boolean equals(Object o) {
    return this == o ||
        !(o == null || getClass() != o.getClass()) && value.equals(((ForwardingWrapper) o).value);

  }

  @Override
  public int hashCode() {
    return value.hashCode();
  }

  @Override
  public String toString() {
    return String.valueOf(value);
  }
}
