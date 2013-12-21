package stupaq.cloudatlas.services.zonemanager;

import java.util.Arrays;
import java.util.List;

public interface ZoneManagerConfigKeys {
  // Runtime configuration section
  static final String PREFIX = "zones";
  // Runtime configuration entries
  static final String REEVALUATION_INTERVAL = PREFIX + ".reevaluation_interval";
  static final long REEVALUATION_INTERVAL_DEFAULT = 5 * 1000L;
  // Static configuration
  static final List<String> RESERVED_NAMES =
      Arrays.asList("level", "name", "owner", "timestamp", "contacts", "cardinality");
}
