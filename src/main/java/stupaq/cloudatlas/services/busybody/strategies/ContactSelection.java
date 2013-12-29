package stupaq.cloudatlas.services.busybody.strategies;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Collection;
import java.util.Set;

import stupaq.cloudatlas.attribute.values.CAContact;
import stupaq.cloudatlas.configuration.CAConfiguration;
import stupaq.cloudatlas.naming.GlobalName;
import stupaq.cloudatlas.plugins.PluginLoader;
import stupaq.cloudatlas.services.zonemanager.ZoneManagementInfo;
import stupaq.cloudatlas.services.zonemanager.builtins.BuiltinAttributesConfigKeys;
import stupaq.cloudatlas.services.zonemanager.hierarchy.ZoneHierarchy;
import stupaq.commons.collect.Collections3;

import static com.google.common.base.Predicates.in;
import static com.google.common.base.Predicates.not;
import static com.google.common.collect.FluentIterable.from;

public class ContactSelection implements ContactSelectionConfigKeys, BuiltinAttributesConfigKeys {
  private static final Log LOG = LogFactory.getLog(ContactSelection.class);
  private final LevelSelection levelStrategy;
  private final ZoneSelection zoneStrategy;
  private final Set<CAContact> blacklisted;

  protected ContactSelection(LevelSelection levelStrategy, ZoneSelection zoneStrategy,
      Set<CAContact> blacklisted) {
    this.levelStrategy = levelStrategy;
    this.zoneStrategy = zoneStrategy;
    this.blacklisted = blacklisted;
  }

  public Optional<CAContact> select(ZoneHierarchy<ZoneManagementInfo> leafZone,
      Collection<CAContact> fallbackContacts) throws Exception {
    Predicate<CAContact> excluded = not(in(blacklisted));
    try {
      ZoneManagementInfo zmi = selectZone(selectParentLevel(leafZone));
      FluentIterable<CAContact> contacts = from(CONTACTS.get(zmi)).filter(excluded);
      if (contacts.isEmpty()) {
        LOG.warn("Selected ZMI has no contacts, picking one from fallback set");
        contacts = from(fallbackContacts).filter(excluded);
      }
      return Optional.fromNullable(
          contacts.isEmpty() ? null : Collections3.<CAContact>random(contacts));
    } catch (Exception e) {
      throw new Exception(e.getMessage(), e);
    }
  }

  private ZoneHierarchy<ZoneManagementInfo> selectParentLevel(
      ZoneHierarchy<ZoneManagementInfo> leafZone) {
    GlobalName leafName = leafZone.globalName();
    int level = levelStrategy.select(leafName);
    Preconditions.checkState(0 < level, "Chosen level must not be a root");
    Preconditions.checkState(level <= leafName.leafLevel(), "Chosen level is below leaf level");
    LOG.info("Selected level: " + level);
    return leafZone.parent(leafName.leafLevel() - level + 1);
  }

  private ZoneManagementInfo selectZone(ZoneHierarchy<ZoneManagementInfo> parent) {
    ZoneManagementInfo zmi = zoneStrategy.select(parent.globalName(), parent.childPayloads());
    LOG.info("Selected zone's local name: " + zmi.localName());
    return zmi;
  }

  public static ContactSelection create(CAConfiguration config, Set<CAContact> blacklisted) {
    return new ContactSelection(createLevel(config), createZone(config), blacklisted);
  }

  private static LevelSelection createLevel(CAConfiguration config) {
    return PluginLoader.initialize(
        config.getPlugin(LEVEL_SELECTION, ContactSelectionConfigKeys.PREFIX,
            LEVEL_SELECTION_DEFAULT), config);
  }

  private static ZoneSelection createZone(CAConfiguration config) {
    return PluginLoader.initialize(
        config.getPlugin(ZONE_SELECTION, ContactSelectionConfigKeys.PREFIX, ZONE_SELECTION_DEFAULT),
        config);
  }
}
