package stupaq.cloudatlas.attribute.types;

/** PACKAGE-LOCAL */
abstract class PrimitiveWrapper<Primitive> {
  private Primitive value;

  protected PrimitiveWrapper(Primitive value) {
    this.value = value;
  }

  public Primitive getValue() {
    return value;
  }

  protected void setValue(Primitive value) {
    this.value = value;
  }

  @Override
  public boolean equals(Object o) {
    PrimitiveWrapper that = (PrimitiveWrapper) o;
    return this == o || !(o == null || getClass() != o.getClass()) && !(value != null ? !value
        .equals(that.value) : that.value != null);
  }

  @Override
  public int hashCode() {
    return value != null ? value.hashCode() : 0;
  }

  @Override
  public String toString() {
    return "PrimitiveWrapper{" +
           "value=" + value +
           '}';
  }
}
