package stupaq.cloudatlas.services.zonemanager.builtins;

import com.google.common.collect.Iterables;

import java.util.Collections;

import stupaq.cloudatlas.attribute.values.CAContact;
import stupaq.cloudatlas.attribute.values.CAInteger;
import stupaq.cloudatlas.attribute.values.CASet;
import stupaq.cloudatlas.attribute.values.CATime;
import stupaq.cloudatlas.query.typecheck.TypeInfo;
import stupaq.cloudatlas.services.zonemanager.ZoneManagementInfo;
import stupaq.cloudatlas.services.zonemanager.hierarchy.ZoneHierarchy.InPlaceSynthesizer;

public class BuiltinsUpdater extends InPlaceSynthesizer<ZoneManagementInfo>
    implements BuiltinAttributesConfigKeys {
  private final CATime time;

  public BuiltinsUpdater(long time) {
    this.time = new CATime(time);
  }

  @Override
  protected void process(Iterable<ZoneManagementInfo> children, ZoneManagementInfo zmi) {
    zmi.setPrime(TIMESTAMP.create(time));
    zmi.setPrime(CONTACTS.create(
        new CASet<>(TypeInfo.of(CAContact.class), Collections.<CAContact>emptySet())));
    if (Iterables.isEmpty(children)) {
      zmi.setPrime(CARDINALITY.create(new CAInteger(1)));
    } else {
      long cardinality = 0;
      for (ZoneManagementInfo child : children) {
        cardinality += CARDINALITY.get(child).getValue().getLong();
      }
      zmi.setPrime(CARDINALITY.create(new CAInteger(cardinality)));
    }
  }
}
