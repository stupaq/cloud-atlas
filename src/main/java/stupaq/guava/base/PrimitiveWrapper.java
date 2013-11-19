package stupaq.guava.base;

import com.google.common.base.Preconditions;

import javax.annotation.Nonnull;

public abstract class PrimitiveWrapper<Primitive> {
  @Nonnull
  private Primitive value;

  protected PrimitiveWrapper(@Nonnull Primitive value) {
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
    return this == o || !(o == null || getClass() != o.getClass()) && value
        .equals(((PrimitiveWrapper) o).value);
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
