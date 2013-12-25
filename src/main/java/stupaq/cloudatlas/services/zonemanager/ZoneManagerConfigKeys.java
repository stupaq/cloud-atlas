package stupaq.cloudatlas.services.zonemanager;

public interface ZoneManagerConfigKeys {
  // Runtime configuration section
  static final String PREFIX = "zones.";
  // Runtime configuration entries
  static final String ZONE_NAME = PREFIX + "zone_name";
  static final String REEVALUATION_INTERVAL = PREFIX + "reevaluation_interval";
  static final long REEVALUATION_INTERVAL_DEFAULT = 5 * 1000L;
  static final String PURGE_INTERVAL = PREFIX + "purge_interval";
  static final long PURGE_INTERVAL_DEFAULT = 60 * 1000L;
  static final String HIERARCHY_DUMP_FILE = PREFIX + "hierarchy_dump_file";
  static final String HIERARCHY_DUMP_SIZE = PREFIX + "hierarchy_dump_size";
  static final long HIERARCHY_DUMP_SIZE_DEFAULT = 1024L * 16;
  // Static configuration
}
