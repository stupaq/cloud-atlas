package stupaq.cloudatlas.services.busybody.strategies;

import stupaq.cloudatlas.services.busybody.strategies.ContactSelection.LevelSelection;
import stupaq.cloudatlas.services.busybody.strategies.ContactSelection.ZoneSelection;

public interface ContactSelectionConfigKeys {
  // Runtime configuration section
  static final String PREFIX = "contact.";
  // Runtime configuration entries
  static final String LEVEL_SELECTION = PREFIX + "level_selection";
  static final Class<? extends LevelSelection> LEVEL_SELECTION_DEFAULT = null;
  static final String ZONE_SELECTION = PREFIX + "zone_selection";
  static final Class<? extends ZoneSelection> ZONE_SELECTION_DEFAULT = null;
  // Static configuration
}
