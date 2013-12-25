package stupaq.cloudatlas.services.zonemanager.purging;

import com.google.common.base.Predicate;

import stupaq.cloudatlas.attribute.values.CATime;
import stupaq.cloudatlas.services.zonemanager.ZoneManagementInfo;
import stupaq.cloudatlas.services.zonemanager.builtins.BuiltinAttributesConfigKeys;

public class StaleZonesRemover
    implements Predicate<ZoneManagementInfo>, BuiltinAttributesConfigKeys {
  private final CATime threshold;

  public StaleZonesRemover(long threshold) {
    this.threshold = new CATime(threshold);
  }

  @Override
  public boolean apply(ZoneManagementInfo zmi) {
    return TIMESTAMP.get(zmi).value().rel().greaterThan(threshold).getOr(true);
  }
}
