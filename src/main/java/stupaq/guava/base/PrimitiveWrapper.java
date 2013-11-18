package stupaq.guava.base;

import com.google.common.base.Preconditions;

public abstract class PrimitiveWrapper<Primitive> {
  private Primitive value;

  protected PrimitiveWrapper(Primitive value) {
    Preconditions.checkNotNull(value);
    this.value = value;
  }

  protected Primitive getValue() {
    return value;
  }

  protected void setValue(Primitive value) {
    Preconditions.checkNotNull(value);
    this.value = value;
  }

  /** {@link java.lang.Object} */
  @Override
  public boolean equals(Object o) {
    return this == o || !(o == null || getClass() != o.getClass()) && value
        .equals(((PrimitiveWrapper) o).value);
  }

  @Override
  public int hashCode() {
    return value != null ? value.hashCode() : 0;
  }

  @Override
  public String toString() {
    return String.valueOf(value);
  }
}
