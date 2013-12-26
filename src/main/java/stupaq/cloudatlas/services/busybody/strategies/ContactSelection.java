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
  private final Collection<CAContact> blacklisted;

  protected ContactSelection(LevelSelection levelStrategy, ZoneSelection zoneStrategy,
      Collection<CAContact> blacklisted) {
    this.levelStrategy = levelStrategy;
    this.zoneStrategy = zoneStrategy;
    this.blacklisted = blacklisted;
  }

  public Optional<CAContact> select(ZoneHierarchy<ZoneManagementInfo> leafZone) throws Exception {
    GlobalName leafName = leafZone.globalName();
    try {
      int level = levelStrategy.select(leafName);
      Preconditions.checkState(0 < level, "Chosen level must not be a root");
      Preconditions.checkState(level <= leafName.level(), "Chosen level is below leaf level");
      LOG.info("Selected level: " + level);
      ZoneHierarchy<ZoneManagementInfo> parent = leafZone.parent(leafName.level() - level + 1);
      ZoneManagementInfo zmi = zoneStrategy.select(parent.globalName(), parent.childPayloads());
      LOG.info("Selected zone's local name: " + zmi.localName());
      FluentIterable<CAContact> contacts =
          from(CONTACTS.get(zmi).value()).filter(not(in(blacklisted)));
      if (contacts.isEmpty()) {
        LOG.warn("Selected ZMI has no contacts, falling back to leaf zone's");
        contacts = from(CONTACTS.get(leafZone.payload()).value());
      }
      return Optional.fromNullable(
          contacts.isEmpty() ? null : Collections3.<CAContact>random(contacts));
    } catch (Exception e) {
      throw new Exception(e.getMessage(), e);
    }
  }

  public static ContactSelection create(CAConfiguration config, Collection<CAContact> blacklisted) {
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
