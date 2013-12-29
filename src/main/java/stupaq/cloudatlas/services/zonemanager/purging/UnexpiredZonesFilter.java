package stupaq.cloudatlas.services.zonemanager.purging;

import com.google.common.base.Predicate;

import java.util.concurrent.TimeUnit;

import stupaq.cloudatlas.attribute.values.CATime;
import stupaq.cloudatlas.configuration.CAConfiguration;
import stupaq.cloudatlas.services.zonemanager.ZoneManagementInfo;
import stupaq.cloudatlas.services.zonemanager.ZoneManagerConfigKeys;
import stupaq.cloudatlas.services.zonemanager.builtins.BuiltinAttributesConfigKeys;
import stupaq.cloudatlas.time.SynchronizedClock;

public class UnexpiredZonesFilter
    implements Predicate<ZoneManagementInfo>, BuiltinAttributesConfigKeys, ZoneManagerConfigKeys {
  private final CATime threshold;

  public UnexpiredZonesFilter(SynchronizedClock clock, CAConfiguration config) {
    threshold = new CATime(clock.timestamp(-config.getLong(PURGE_INTERVAL, PURGE_INTERVAL_DEFAULT),
        TimeUnit.MILLISECONDS));
  }

  @Override
  public boolean apply(ZoneManagementInfo zmi) {
    return zmi.isOlderThan(threshold).op().not().getOr(true);
  }
}
