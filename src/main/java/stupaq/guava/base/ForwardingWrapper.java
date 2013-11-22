package stupaq.guava.base;

import com.google.common.base.Preconditions;

import javax.annotation.Nonnull;

/**
 * General purpose class that simplifies extending final classes by forwarding {@link
 * #equals(Object)}, {@link #hashCode()} and {@link #toString()}.
 */
public abstract class ForwardingWrapper<Primitive> {
  @Nonnull private Primitive value;

  protected ForwardingWrapper(@Nonnull Primitive value) {
    Preconditions.checkNotNull(value);
    this.value = value;
  }

  @Nonnull
  protected Primitive get() {
    return value;
  }

  protected void set(@Nonnull Primitive value) {
    Preconditions.checkNotNull(value);
    this.value = value;
  }

  @Override
  public boolean equals(Object o) {
    return this == o ||
        o instanceof ForwardingWrapper && value.equals(((ForwardingWrapper) o).value);

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