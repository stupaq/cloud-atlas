package stupaq.cloudatlas.services.zonemanager.query;

import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.collect.Sets;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import stupaq.cloudatlas.attribute.Attribute;
import stupaq.cloudatlas.attribute.AttributeValue;
import stupaq.cloudatlas.naming.AttributeName;
import stupaq.cloudatlas.query.errors.EvaluationException;
import stupaq.cloudatlas.query.evaluation.context.OutputContext;
import stupaq.cloudatlas.query.semantics.values.RSingle;
import stupaq.cloudatlas.services.zonemanager.ZoneManagementInfo;
import stupaq.cloudatlas.services.zonemanager.ZoneManagerConfigKeys;

import static com.google.common.collect.FluentIterable.from;

/** PACKAGE-LOCAL */
class ZMIUpdaterOutputContext implements OutputContext, ZoneManagerConfigKeys {
  private final Set<AttributeName> reservedNames =
      Sets.newHashSet(from(RESERVED_NAMES).transform(new Function<String, AttributeName>() {
        @Override
        public AttributeName apply(String str) {
          return AttributeName.valueOf(str);
        }
      }));
  private final ZoneManagementInfo destination;
  private final List<Attribute> putsLog;

  public ZMIUpdaterOutputContext(ZoneManagementInfo destination) {
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
      name = AttributeName.valueOf(nameStr);
    } catch (IllegalArgumentException e) {
      throw new EvaluationException(e.getMessage());
    }
    if (reservedNames.contains(name)) {
      throw new EvaluationException("Name: " + name + " is reserved");
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
