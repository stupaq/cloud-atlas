package stupaq.cloudatlas.services.busybody;

public interface BusybodyConfigKeys {
  // Runtime configuration section
  static final String PREFIX = "gossip.";
  // Runtime configuration entries
  static final String ROUND_INTERVAL = PREFIX + "round_interval";
  static final long ROUND_INTERVAL_DEFAULT = 5 * 1000L;
  static final String LEVEL_SELECTION = PREFIX + "level_selection";
  static final String LEVEL_SELECTION_DEFAULT = null;
  static final String ZONE_SELECTION = PREFIX + "zone_selection";
  static final String ZONE_SELECTION_DEFAULT = null;
  // Static configuration
}
