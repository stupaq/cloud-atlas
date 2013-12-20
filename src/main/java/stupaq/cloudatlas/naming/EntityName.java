package stupaq.cloudatlas.naming;

import com.google.common.base.Preconditions;

public class EntityName {
  public final GlobalName zone;
  public final AttributeName attributeName;

  public EntityName(GlobalName zone, AttributeName attributeName) {
    this.zone = zone;
    this.attributeName = attributeName;
  }

  public static EntityName parse(String str) {
    String[] parts = str.split(GlobalName.SEPARATOR);
    Preconditions.checkArgument(parts.length == 2);
    return new EntityName(GlobalName.parse(parts[0]), AttributeName.valueOf(parts[1]));
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
