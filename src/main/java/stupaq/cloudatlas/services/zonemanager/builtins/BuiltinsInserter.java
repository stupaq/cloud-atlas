package stupaq.cloudatlas.services.zonemanager.builtins;

import stupaq.cloudatlas.attribute.Attribute;
import stupaq.cloudatlas.attribute.AttributeValue;
import stupaq.cloudatlas.attribute.values.CAInteger;
import stupaq.cloudatlas.attribute.values.CAString;
import stupaq.cloudatlas.naming.AttributeName;
import stupaq.cloudatlas.naming.GlobalName;
import stupaq.cloudatlas.naming.LocalName;
import stupaq.cloudatlas.services.zonemanager.ZoneManagementInfo;
import stupaq.cloudatlas.services.zonemanager.hierarchy.ZoneHierarchy.Inserter;

public class BuiltinsInserter extends Inserter<ZoneManagementInfo> {
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
    set(zmi, "level", new CAInteger(level++));
    set(zmi, "name", new CAString(local.isRoot()? null:local.toString()));
    set(zmi, "owner", new CAString(owner));
    return zmi;
  }

  private static void set(ZoneManagementInfo zmi, String name, AttributeValue value) {
    zmi.setPrime(new Attribute<>(AttributeName.valueOf(name), value));
  }
}
