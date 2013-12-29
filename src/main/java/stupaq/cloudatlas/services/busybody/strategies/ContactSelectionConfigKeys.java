package stupaq.cloudatlas.services.busybody.strategies;

import stupaq.cloudatlas.plugins.contact.RandomZone;
import stupaq.cloudatlas.plugins.contact.UniformLevel;

public interface ContactSelectionConfigKeys {
  // Runtime configuration section
  static final String PREFIX = "contact_selection.";
  // Runtime configuration entries
  static final String LEVEL_SELECTION = PREFIX + "level_selection";
  static final Class<? extends LevelSelection> LEVEL_SELECTION_DEFAULT = UniformLevel.class;
  static final String ZONE_SELECTION = PREFIX + "zone_selection";
  static final Class<? extends ZoneSelection> ZONE_SELECTION_DEFAULT = RandomZone.class;
  // Static configuration
}
