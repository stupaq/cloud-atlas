package stupaq.cloudatlas.services.zonemanager.builtins;

import java.util.Collections;

import stupaq.cloudatlas.attribute.Attribute;
import stupaq.cloudatlas.attribute.AttributeValue;
import stupaq.cloudatlas.attribute.values.CAContact;
import stupaq.cloudatlas.attribute.values.CAInteger;
import stupaq.cloudatlas.attribute.values.CASet;
import stupaq.cloudatlas.attribute.values.CATime;
import stupaq.cloudatlas.query.typecheck.TypeInfo;
import stupaq.cloudatlas.services.zonemanager.ZoneManagementInfo;
import stupaq.cloudatlas.services.zonemanager.ZoneManagerConfigKeys;
import stupaq.cloudatlas.services.zonemanager.hierarchy.ZoneHierarchy.InPlaceSynthesizer;

public class BuiltinsUpdater extends InPlaceSynthesizer<ZoneManagementInfo>
    implements ZoneManagerConfigKeys {
  private final CATime time;

  public BuiltinsUpdater(long time) {
    this.time = new CATime(time);
  }

  @Override
  protected void process(Iterable<ZoneManagementInfo> children, ZoneManagementInfo zmi) {
    zmi.setPrime(new Attribute<>(TIMESTAMP, time));
    zmi.setPrime(new Attribute<>(CONTACTS,
        new CASet<>(TypeInfo.of(CAContact.class), Collections.<CAContact>emptySet())));
    AttributeValue cardinality = new CAInteger(1);
    for (ZoneManagementInfo child : children) {
      cardinality = cardinality.op()
          .add(child.get(CARDINALITY).or(new Attribute<>(CARDINALITY, new CAInteger())).getValue());
    }
    zmi.setPrime(new Attribute<>(CARDINALITY, cardinality));
  }
}
