package stupaq.cloudatlas.naming;

import java.io.Serializable;

import stupaq.compact.SerializationConstructor;

public class EntityName implements Serializable {
  private static final long serialVersionUID = 1L;
  public final GlobalName zone;
  public final AttributeName attributeName;

  @SerializationConstructor
  protected EntityName() {
    zone = null;
    attributeName = null;
  }

  public EntityName(GlobalName zone, AttributeName attributeName) {
    this.zone = zone;
    this.attributeName = attributeName;
  }

  public static EntityName parse(String str) {
    GlobalName entity = GlobalName.parse(str);
    return new EntityName(entity.parent(), AttributeName.valueOf(entity.child().toString()));
  }

  @Override
  public String toString() {
    return zone.toString() + GlobalName.SEPARATOR + attributeName.toString();
  }

  @Override
  public boolean equals(Object o) {
    return this == o || !(o == null || getClass() != o.getClass()) &&
        attributeName.equals(((EntityName) o).attributeName) && zone.equals(((EntityName) o).zone);

  }

  @Override
  public int hashCode() {
    int result = zone.hashCode();
    result = 31 * result + attributeName.hashCode();
    return result;
  }
}
