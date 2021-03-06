package stupaq.cloudatlas.services.zonemanager.query;

import com.google.common.base.Preconditions;

import java.util.ArrayList;
import java.util.List;

import stupaq.cloudatlas.attribute.Attribute;
import stupaq.cloudatlas.attribute.AttributeValue;
import stupaq.cloudatlas.naming.AttributeName;
import stupaq.cloudatlas.query.errors.EvaluationException;
import stupaq.cloudatlas.query.evaluation.context.OutputContext;
import stupaq.cloudatlas.query.semantics.values.RSingle;
import stupaq.cloudatlas.services.zonemanager.ZoneManagementInfo;
import stupaq.cloudatlas.services.zonemanager.ZoneManagerConfigKeys;
import stupaq.cloudatlas.services.zonemanager.builtins.BuiltinAttribute;

/** PACKAGE-LOCAL */
class ZoneUpdaterOutputContext implements OutputContext, ZoneManagerConfigKeys {
  private final ZoneManagementInfo destination;
  private final List<Attribute> putsLog;

  public ZoneUpdaterOutputContext(ZoneManagementInfo destination) {
    this.destination = destination;
    this.putsLog = new ArrayList<>();
  }

  @Override
  public void put(String nameStr, RSingle<? extends AttributeValue> value) {
    Preconditions.checkNotNull(nameStr);
    Preconditions.checkNotNull(value);
    // Attribute value cannot start with reserved prefix
    AttributeName name;
    try {
      name = AttributeName.fromString(nameStr);
    } catch (IllegalArgumentException e) {
      throw new EvaluationException(e.getMessage());
    }
    if (BuiltinAttribute.isWriteProtected(name)) {
      throw new EvaluationException(
          "Name: " + name + " is write-protected as a built-in attribute");
    }
    Attribute attribute = new Attribute<>(name, value.get());
    putsLog.add(attribute);
  }

  @Override
  public void commit() {
    for (Attribute attribute : putsLog) {
      destination.setComputed(attribute);
    }
    putsLog.clear();
  }
}
