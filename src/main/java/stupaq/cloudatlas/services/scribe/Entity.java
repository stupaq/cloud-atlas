package stupaq.cloudatlas.services.scribe;

import com.google.common.base.Preconditions;

import stupaq.cloudatlas.attribute.AttributeName;
import stupaq.cloudatlas.naming.GlobalName;

public class Entity {
  public static final String DELIMITER = "#";
  public final GlobalName zone;
  public final AttributeName attributeName;

  public Entity(GlobalName zone, AttributeName attributeName) {
    this.zone = zone;
    this.attributeName = attributeName;
  }

  public static Entity parse(String str) {
    String[] parts = str.split(DELIMITER);
    Preconditions.checkArgument(parts.length == 2);
    return new Entity(GlobalName.parse(parts[0]), AttributeName.valueOf(parts[1]));
  }

  @Override
  public boolean equals(Object o) {
    return this == o || !(o == null || getClass() != o.getClass()) &&
        attributeName.equals(((Entity) o).attributeName) && zone.equals(((Entity) o).zone);

  }

  @Override
  public int hashCode() {
    int result = zone.hashCode();
    result = 31 * result + attributeName.hashCode();
    return result;
  }
}
