package stupaq.cloudatlas.interpreter.data;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.collect.FluentIterable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import stupaq.cloudatlas.attribute.Attribute;
import stupaq.cloudatlas.attribute.AttributeName;
import stupaq.cloudatlas.attribute.AttributeValue;
import stupaq.cloudatlas.zone.ZoneManagementInfo;

public class AttributesTable extends ArrayList<AttributesRow> {
  public AttributesTable(AttributesTable table) {
    super(table);
  }

  public AttributesTable(AttributesRow row) {
    add(row);
  }

  public AttributesTable(Iterable<ZoneManagementInfo> zones) {
    fill(FluentIterable.from(zones)
        .transform(new Function<ZoneManagementInfo, Collection<Attribute>>() {
          @Override
          public Collection<Attribute> apply(ZoneManagementInfo managementInfo) {
            return managementInfo.getAttributes();
          }
        }));
  }

  private void fill(Iterable<Collection<Attribute>> subZones) {
    Set<AttributeName> allAttributes = new HashSet<>();
    for (Collection<Attribute> zone : subZones) {
      AttributesRow row = new AttributesRow();
      for (Attribute attribute : zone) {
        row.put(attribute.getName(), attribute.getValue());
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

  @Override
  public String toString() {
    // TODO oh God!
    return super.toString().replace("}, {", "\n\t\t").replace("[{", "value:\t").replace("}]", "");
  }
}
