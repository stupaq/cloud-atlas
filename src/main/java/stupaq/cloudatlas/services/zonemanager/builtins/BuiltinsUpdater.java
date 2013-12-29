package stupaq.cloudatlas.services.zonemanager.builtins;

import com.google.common.collect.Iterables;

import stupaq.cloudatlas.attribute.values.CAInteger;
import stupaq.cloudatlas.attribute.values.CATime;
import stupaq.cloudatlas.services.zonemanager.ZoneManagementInfo;
import stupaq.cloudatlas.services.zonemanager.hierarchy.ZoneHierarchy.InPlaceSynthesizer;
import stupaq.cloudatlas.time.LocalClock;

public class BuiltinsUpdater extends InPlaceSynthesizer<ZoneManagementInfo>
    implements BuiltinAttributesConfigKeys {
  private final CATime time;

  public BuiltinsUpdater(LocalClock clock) {
    time = new CATime(clock.timestamp());
  }

  @Override
  protected void process(Iterable<ZoneManagementInfo> children, ZoneManagementInfo zmi) {
    zmi.setPrime(TIMESTAMP.create(time));
    if (Iterables.isEmpty(children)) {
      zmi.setPrime(CARDINALITY.create(new CAInteger(1)));
    } else {
      long cardinality = 0;
      for (ZoneManagementInfo child : children) {
        cardinality += CARDINALITY.get(child).getLong();
      }
      zmi.setPrime(CARDINALITY.create(new CAInteger(cardinality)));
    }
  }
}
