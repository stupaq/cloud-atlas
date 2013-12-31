package stupaq.cloudatlas.services.zonemanager.builtins;

import java.util.Collection;
import java.util.Collections;

import stupaq.cloudatlas.attribute.values.CAContact;
import stupaq.cloudatlas.attribute.values.CAInteger;
import stupaq.cloudatlas.attribute.values.CASet;
import stupaq.cloudatlas.attribute.values.CAString;
import stupaq.cloudatlas.configuration.CAConfiguration;
import stupaq.cloudatlas.naming.GlobalName;
import stupaq.cloudatlas.naming.LocalName;
import stupaq.cloudatlas.services.busybody.BusybodyConfigKeys;
import stupaq.cloudatlas.services.zonemanager.ZoneManagementInfo;
import stupaq.cloudatlas.services.zonemanager.ZoneManagerConfigKeys;
import stupaq.cloudatlas.services.zonemanager.hierarchy.ZoneHierarchy.Inserter;

import static stupaq.cloudatlas.query.typecheck.TypeInfo.of;

public class BuiltinsInserter extends Inserter<ZoneManagementInfo>
    implements BuiltinAttributesConfigKeys, BusybodyConfigKeys, ZoneManagerConfigKeys {
  private final int leafLevel;
  private final CAString owner;
  private final CAContact selfContact;
  private int level = 0;

  public BuiltinsInserter(CAConfiguration config) {
    GlobalName agentsName = config.getGlobalName(ZONE_NAME);
    leafLevel = agentsName.leafLevel();
    owner = new CAString(agentsName.toString());
    Collection<CAContact> contacts;
    selfContact = config.getLocalContact();
  }

  @Override
  public void descend(LocalName local) {
    level++;
  }

  @Override
  public ZoneManagementInfo create(LocalName local) {
    try {
      ZoneManagementInfo zmi = new ZoneManagementInfo(local);
      zmi.setPrime(LEVEL.create(new CAInteger(level)));
      zmi.setPrime(NAME.create(new CAString(local.isRoot() ? null : local.toString())));
      zmi.setPrime(OWNER.create(owner));
      Collection<CAContact> initialContacts =
          leafLevel == level && selfContact != null ? Collections.singleton(selfContact)
              : Collections.<CAContact>emptySet();
      zmi.setPrime(CONTACTS.create(new CASet<>(of(CAContact.class), initialContacts)));
      return zmi;
    } finally {
      // Perform same actions as when descending
      descend(local);
    }
  }
}
