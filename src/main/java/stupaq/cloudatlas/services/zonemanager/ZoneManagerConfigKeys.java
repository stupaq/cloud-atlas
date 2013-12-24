package stupaq.cloudatlas.services.zonemanager;

import java.util.Arrays;
import java.util.List;

import stupaq.cloudatlas.naming.AttributeName;

import static stupaq.cloudatlas.naming.AttributeName.fromString;

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
  static final AttributeName LEVEL = fromString("level");
  static final AttributeName NAME = fromString("name");
  static final AttributeName OWNER = fromString("owner");
  static final AttributeName CONTACTS = fromString("contacts");
  static final AttributeName TIMESTAMP = fromString("timestamp");
  static final AttributeName CARDINALITY = fromString("cardinality");
  static final List<AttributeName> BUILTIN_ATTRIBUTES =
      Arrays.asList(LEVEL, NAME, OWNER, CONTACTS, TIMESTAMP, CARDINALITY);
}
