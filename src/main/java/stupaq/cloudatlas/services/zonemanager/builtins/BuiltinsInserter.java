package stupaq.cloudatlas.services.zonemanager.builtins;

import java.util.Collections;

import stupaq.cloudatlas.attribute.values.CAContact;
import stupaq.cloudatlas.attribute.values.CAInteger;
import stupaq.cloudatlas.attribute.values.CASet;
import stupaq.cloudatlas.attribute.values.CAString;
import stupaq.cloudatlas.naming.GlobalName;
import stupaq.cloudatlas.naming.LocalName;
import stupaq.cloudatlas.services.zonemanager.ZoneManagementInfo;
import stupaq.cloudatlas.services.zonemanager.hierarchy.ZoneHierarchy.Inserter;

import static stupaq.cloudatlas.query.typecheck.TypeInfo.of;

public class BuiltinsInserter extends Inserter<ZoneManagementInfo>
    implements BuiltinAttributesConfigKeys {
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
    zmi.setPrime(LEVEL.create(new CAInteger(level++)));
    zmi.setPrime(NAME.create(new CAString(local.isRoot() ? null : local.toString())));
    zmi.setPrime(OWNER.create(new CAString(owner)));
    zmi.setPrime(
        CONTACTS.create(new CASet<>(of(CAContact.class), Collections.<CAContact>emptySet())));
    return zmi;
  }
}
