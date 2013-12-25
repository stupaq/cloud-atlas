package stupaq.cloudatlas.services.busybody.strategies;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.collect.FluentIterable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Collection;

import stupaq.cloudatlas.attribute.values.CAContact;
import stupaq.cloudatlas.configuration.CAConfiguration;
import stupaq.cloudatlas.naming.GlobalName;
import stupaq.cloudatlas.plugins.Plugin;
import stupaq.cloudatlas.services.zonemanager.ZoneManagementInfo;
import stupaq.cloudatlas.services.zonemanager.builtins.BuiltinAttributesConfigKeys;
import stupaq.cloudatlas.services.zonemanager.hierarchy.ZoneHierarchy;
import stupaq.commons.collect.Collections3;

public class ContactSelection implements ContactSelectionConfigKeys, BuiltinAttributesConfigKeys {
  private static final Log LOG = LogFactory.getLog(ContactSelection.class);
  private final LevelSelection levelStrategy;
  private final ZoneSelection zoneStrategy;

  public ContactSelection(LevelSelection levelStrategy, ZoneSelection zoneStrategy) {
    this.levelStrategy = levelStrategy;
    this.zoneStrategy = zoneStrategy;
  }

  public Optional<CAContact> select(ZoneHierarchy<ZoneManagementInfo> leafZone) throws Exception {
    GlobalName leafName = leafZone.globalName();
    try {
      int level = levelStrategy.select(leafName);
      Preconditions.checkState(0 < level, "Chosen level must not be a root");
      Preconditions.checkState(level <= leafName.leafLevel(), "Chosen level is below leaf level");
      LOG.info("Selected level: " + level);
      ZoneHierarchy<ZoneManagementInfo> parent = leafZone.parent(leafName.leafLevel() - level + 1);
      ZoneManagementInfo zmi = zoneStrategy.select(parent.childPayloads());
      LOG.info("Selected zone's local name: " + zmi.localName());
      Collection<CAContact> contacts = CONTACTS.get(zmi).getValue();
      return Optional.fromNullable(
          contacts.isEmpty() ? null : Collections3.<CAContact>random(contacts));
    } catch (Exception e) {
      throw new Exception(e.getMessage(), e);
    }
  }

  public static ContactSelection create(CAConfiguration config) {
    return new ContactSelection(createLevel(config), createZone(config));
  }

  private static LevelSelection createLevel(CAConfiguration config) {
    return Plugin.initialize(config.getPlugin(LEVEL_SELECTION, ContactSelectionConfigKeys.PREFIX,
        LEVEL_SELECTION_DEFAULT), config);
  }

  private static ZoneSelection createZone(CAConfiguration config) {
    return Plugin.initialize(
        config.getPlugin(ZONE_SELECTION, ContactSelectionConfigKeys.PREFIX, ZONE_SELECTION_DEFAULT),
        config);
  }

  public static interface LevelSelection {
    public int select(GlobalName name);
  }

  public static interface ZoneSelection {
    public ZoneManagementInfo select(FluentIterable<ZoneManagementInfo> zones);
  }
}
