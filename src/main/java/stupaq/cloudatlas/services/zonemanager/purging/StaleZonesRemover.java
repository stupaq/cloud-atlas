package stupaq.cloudatlas.services.zonemanager.purging;

import com.google.common.base.Optional;
import com.google.common.base.Predicate;

import stupaq.cloudatlas.attribute.Attribute;
import stupaq.cloudatlas.attribute.values.CATime;
import stupaq.cloudatlas.services.zonemanager.ZoneManagementInfo;
import stupaq.cloudatlas.services.zonemanager.ZoneManagerConfigKeys;

public class StaleZonesRemover implements Predicate<ZoneManagementInfo>, ZoneManagerConfigKeys {
  private final CATime threshold;

  public StaleZonesRemover(long threshold) {
    this.threshold = new CATime(threshold);
  }

  @Override
  public boolean apply(ZoneManagementInfo zmi) {
    Optional<Attribute> timestamp = zmi.get(TIMESTAMP);
    return !timestamp.isPresent() ||
        timestamp.get().getValue().rel().greaterThan(threshold).getOr(true);
  }
}
