package stupaq.cloudatlas.plugins.contact;

import com.google.common.collect.FluentIterable;

import java.util.Random;

import stupaq.cloudatlas.configuration.CAConfiguration;
import stupaq.cloudatlas.services.busybody.strategies.ContactSelection.ZoneSelection;
import stupaq.cloudatlas.services.zonemanager.ZoneManagementInfo;
import stupaq.commons.collect.Collections3;

public class RandomZone implements ZoneSelection {
  private final Random random = new Random();

  public RandomZone(CAConfiguration config) {
  }

  @Override
  public ZoneManagementInfo select(FluentIterable<ZoneManagementInfo> zones) {
    return Collections3.random(zones, random);
  }
}
