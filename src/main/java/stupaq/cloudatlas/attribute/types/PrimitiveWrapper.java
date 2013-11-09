package stupaq.cloudatlas.attribute.types;

import com.google.common.base.Preconditions;

/** PACKAGE-LOCAL */
abstract class PrimitiveWrapper<Primitive> {
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
  public int hashCode() {
    return value != null ? value.hashCode() : 0;
  }

  @Override
  public boolean equals(Object o) {
    PrimitiveWrapper that = (PrimitiveWrapper) o;
    return this == o || !(o == null || getClass() != o.getClass()) && !(value != null ? !value
        .equals(that.value) : that.value != null);
  }

  @Override
  public String toString() {
    return String.valueOf(value);
  }
}
