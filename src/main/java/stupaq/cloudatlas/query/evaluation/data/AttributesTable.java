package stupaq.cloudatlas.query.evaluation.data;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import stupaq.cloudatlas.attribute.Attribute;
import stupaq.cloudatlas.attribute.AttributeValue;
import stupaq.cloudatlas.naming.AttributeName;
import stupaq.cloudatlas.query.errors.TypeCheckerException;
import stupaq.cloudatlas.query.typecheck.TypeInfo;

public class AttributesTable extends ArrayList<AttributesRow> {
  private HashMap<AttributeName, TypeInfo> inputTypes = null;

  public AttributesTable(AttributesTable table) {
    super(table);
    this.inputTypes = table.inputTypes;
  }

  public AttributesTable(Iterable<Iterable<Attribute>> subZones) {
    Preconditions.checkState(inputTypes == null, "Table already filled");
    inputTypes = Maps.newHashMap();
    for (Iterable<Attribute> zone : subZones) {
      AttributesRow row = new AttributesRow();
      for (Attribute attribute : zone) {
        AttributeName name = attribute.name();
        AttributeValue value = attribute.value();
        TypeInfo known = inputTypes.get(name);
        if (known == null) {
          known = value.type();
          inputTypes.put(name, known);
        }
        if (!known.equals(value.type())) {
          throw new TypeCheckerException("AttributeName: " + name + " maps to not matching types.");
        }
        row.put(name, attribute.value());
      }
      add(row);
    }
    for (AttributesRow row : this) {
      for (Map.Entry<AttributeName, TypeInfo> entry : inputTypes.entrySet()) {
        if (!row.containsKey(entry.getKey())) {
          row.put(entry.getKey(), entry.getValue().aNull());
        }
      }
    }
  }

  public Set<Entry<AttributeName, TypeInfo>> getTypes() {
    return inputTypes.entrySet();
  }
}
