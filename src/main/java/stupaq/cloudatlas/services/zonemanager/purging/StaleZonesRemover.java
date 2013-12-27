package stupaq.cloudatlas.services.zonemanager.purging;

import com.google.common.base.Predicate;

import java.util.concurrent.TimeUnit;

import stupaq.cloudatlas.attribute.values.CATime;
import stupaq.cloudatlas.configuration.CAConfiguration;
import stupaq.cloudatlas.services.zonemanager.ZoneManagementInfo;
import stupaq.cloudatlas.services.zonemanager.ZoneManagerConfigKeys;
import stupaq.cloudatlas.services.zonemanager.builtins.BuiltinAttributesConfigKeys;
import stupaq.cloudatlas.time.LocalClock;

public class StaleZonesRemover
    implements Predicate<ZoneManagementInfo>, BuiltinAttributesConfigKeys, ZoneManagerConfigKeys {
  private final CATime threshold;

  public StaleZonesRemover(LocalClock clock, CAConfiguration config) {
    threshold = new CATime(clock.timestamp(-config.getLong(PURGE_INTERVAL, PURGE_INTERVAL_DEFAULT),
        TimeUnit.MILLISECONDS));
  }

  @Override
  public boolean apply(ZoneManagementInfo zmi) {
    return TIMESTAMP.get(zmi).value().rel().greaterThan(threshold).getOr(true);
  }
}
