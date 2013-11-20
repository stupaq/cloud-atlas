package stupaq.cloudatlas.interpreter.evaluation.data;

import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.collect.FluentIterable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import stupaq.cloudatlas.attribute.Attribute;
import stupaq.cloudatlas.attribute.AttributeName;
import stupaq.cloudatlas.attribute.AttributeValue;
import stupaq.cloudatlas.interpreter.errors.TypeCheckerException;
import stupaq.cloudatlas.interpreter.typecheck.TypeInfo;
import stupaq.cloudatlas.zone.ZoneManagementInfo;

public class AttributesTable extends ArrayList<AttributesRow> {
  private HashMap<AttributeName, TypeInfo> inputTypes = null;

  public AttributesTable(AttributesTable table) {
    super(table);
    this.inputTypes = table.inputTypes;
  }

  public AttributesTable(Iterable<ZoneManagementInfo> zones) {
    fillFrom(FluentIterable.from(zones)
        .transform(new Function<ZoneManagementInfo, Collection<Attribute>>() {
          @Override
          public Collection<Attribute> apply(ZoneManagementInfo managementInfo) {
            return managementInfo.getPublicAttributes();
          }
        }));
  }

  private void fillFrom(Iterable<Collection<Attribute>> subZones) {
    Preconditions.checkState(inputTypes == null, "Table already filled");
    inputTypes = new HashMap<>();
    for (Collection<Attribute> zone : subZones) {
      AttributesRow row = new AttributesRow();
      for (Attribute attribute : zone) {
        AttributeName name = attribute.getName();
        AttributeValue value = attribute.getValue();
        TypeInfo known = inputTypes.get(name);
        if (known == null) {
          known = value.getType();
          inputTypes.put(name, known);
        }
        if (!known.equals(value.getType())) {
          throw new TypeCheckerException("AttributeName: " + name + " maps to not matching types.");
        }
        row.put(name, attribute.getValue());
      }
      add(row);
    }
    for (AttributesRow row : this) {
      for (Map.Entry<AttributeName, TypeInfo> entry : inputTypes.entrySet()) {
        if (!row.containsKey(entry.getKey())) {
          row.put(entry.getKey(), entry.getValue().Null());
        }
      }
    }
  }

  public Set<Entry<AttributeName, TypeInfo>> getTypes() {
    return inputTypes.entrySet();
  }

  @Override
  public String toString() {
    // TODO oh God!
    return super.toString().replace("}, {", "\n\t\t").replace("[{", "value:\t").replace("}]", "");
  }
}
