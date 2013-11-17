package stupaq.cloudatlas.interpreter.data;

import com.google.common.base.Optional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import stupaq.cloudatlas.attribute.Attribute;
import stupaq.cloudatlas.attribute.AttributeName;
import stupaq.cloudatlas.attribute.AttributeValue;

public class AttributesTable extends ArrayList<AttributesRow> {
  public AttributesTable(AttributesTable table) {
    super(table);
  }

  public AttributesTable(Iterable<Set<Attribute>> subZones) {
    // TODO collect types
    Set<AttributeName> allAttributes = new HashSet<>();
    for (Set<Attribute> zone : subZones) {
      AttributesRow row = new AttributesRow();
      for (Attribute attribute : zone) {
        row.put(attribute.getName(), Optional.of(attribute.getValue()));
        allAttributes.add(attribute.getName());
      }
      add(row);
    }
    for (AttributesRow row : this) {
      for (AttributeName name : allAttributes) {
        if (!row.containsKey(name)) {
          row.put(name, Optional.<AttributeValue>absent());
        }
      }
    }
  }
}
