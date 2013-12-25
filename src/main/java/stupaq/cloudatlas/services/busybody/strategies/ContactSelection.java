package stupaq.cloudatlas.services.busybody.strategies;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import stupaq.cloudatlas.attribute.Attribute;
import stupaq.cloudatlas.attribute.values.CAContact;
import stupaq.cloudatlas.attribute.values.CASet;
import stupaq.cloudatlas.configuration.CAConfiguration;
import stupaq.cloudatlas.naming.GlobalName;
import stupaq.cloudatlas.plugins.Plugin;
import stupaq.cloudatlas.query.typecheck.ComposedTypeInfo;
import stupaq.cloudatlas.query.typecheck.TypeInfo;
import stupaq.cloudatlas.services.zonemanager.ZoneManagementInfo;
import stupaq.cloudatlas.services.zonemanager.ZoneManagerConfigKeys;
import stupaq.cloudatlas.services.zonemanager.hierarchy.ZoneHierarchy;
import stupaq.commons.collect.Collections3;

public class ContactSelection implements ContactSelectionConfigKeys, ZoneManagerConfigKeys {
  private static final Log LOG = LogFactory.getLog(ContactSelection.class);
  private final LevelSelection levelStrategy;
  private final ZoneSelection zoneStrategy;

  public ContactSelection(LevelSelection levelStrategy, ZoneSelection zoneStrategy) {
    this.levelStrategy = levelStrategy;
    this.zoneStrategy = zoneStrategy;
  }

  public CAContact select(ZoneHierarchy<ZoneManagementInfo> leafZone) throws Exception {
    try {
      GlobalName leafName = leafZone.globalName();
      int level = levelStrategy.select(leafName);
      Preconditions.checkState(0 < level, "Chosen level must not be a root");
      Preconditions.checkState(level <= leafName.leafLevel(), "Chosen level is below leaf level");
      ZoneHierarchy<ZoneManagementInfo> parent = leafZone.parent(leafName.leafLevel() - level + 1);
      ZoneManagementInfo zmi = zoneStrategy.select(parent.childPayloads());
      Optional<Attribute<CASet>> attribute =
          zmi.get(CONTACTS, ComposedTypeInfo.of(CASet.class, TypeInfo.of(CAContact.class)));
      Preconditions.checkState(attribute.isPresent());
      return (CAContact) Collections3.random(attribute.get().getValue().get());
    } catch (Exception e) {
      throw new Exception(e.getMessage());
    }
  }

  public static ContactSelection create(CAConfiguration config) {
    return new ContactSelection(createLevel(config), createZone(config));
  }

  private static LevelSelection createLevel(CAConfiguration config) {
    return Plugin.initialize(config.getPlugin(LEVEL_SELECTION, LEVEL_SELECTION_DEFAULT), config);
  }

  private static ZoneSelection createZone(CAConfiguration config) {
    return Plugin.initialize(config.getPlugin(ZONE_SELECTION, ZONE_SELECTION_DEFAULT), config);
  }

  public static interface LevelSelection {
    public int select(GlobalName name);
  }

  public static interface ZoneSelection {
    public ZoneManagementInfo select(Iterable<ZoneManagementInfo> zones);
  }
}
