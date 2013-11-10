package stupaq.cloudatlas;

import com.google.common.base.Preconditions;

public abstract class PrimitiveWrapper<Primitive> {
  private Primitive value;

  protected PrimitiveWrapper(Primitive value) {
    Preconditions.checkNotNull(value);
    this.value = value;
  }

  @SuppressWarnings("unused")
  protected PrimitiveWrapper(Primitive value, Void safeNullValue) {
    this.value = value;
  }

  public Primitive getValue() {
    return value;
  }

  protected void setValue(Primitive value) {
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
    return value != null ? value.hashCode() : 0;
  }

  @Override
  public String toString() {
    return String.valueOf(value);
  }
}
