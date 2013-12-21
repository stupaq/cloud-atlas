package stupaq.cloudatlas.services.zonemanager.builtins;

import stupaq.cloudatlas.attribute.Attribute;
import stupaq.cloudatlas.attribute.values.CAInteger;
import stupaq.cloudatlas.attribute.values.CAString;
import stupaq.cloudatlas.naming.GlobalName;
import stupaq.cloudatlas.naming.LocalName;
import stupaq.cloudatlas.services.zonemanager.ZoneManagementInfo;
import stupaq.cloudatlas.services.zonemanager.ZoneManagerConfigKeys;
import stupaq.cloudatlas.services.zonemanager.hierarchy.ZoneHierarchy.Inserter;

public class BuiltinsInserter extends Inserter<ZoneManagementInfo>
    implements ZoneManagerConfigKeys {
  private final String owner;
  private int level = 0;

  public BuiltinsInserter(GlobalName owner) {
    this.owner = owner.toString();
  }

  @Override
  public void descend(LocalName local) {
    level++;
  }

  @Override
  public ZoneManagementInfo create(LocalName local) {
    ZoneManagementInfo zmi = new ZoneManagementInfo(local);
    zmi.setPrime(new Attribute<>(LEVEL, new CAInteger(level++)));
    zmi.setPrime(new Attribute<>(NAME, new CAString(local.isRoot() ? null : local.toString())));
    zmi.setPrime(new Attribute<>(OWNER, new CAString(owner)));
    return zmi;
  }
}
