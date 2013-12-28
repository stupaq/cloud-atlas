package stupaq.cloudatlas.services.collector;

import java.util.Arrays;
import java.util.List;

interface AttributesCollectorConfigKeys {
  // Runtime configuration section
  static final String PREFIX = "collector.";
  // Runtime configuration entries
  static final String ZONE_NAME = PREFIX + "zone_name";
  static final String PUSH_INTERVAL = PREFIX + "push_interval";
  static final long PUSH_INTERVAL_DEFAULT = 5 * 1000L;
  static final String SCRIPT = PREFIX + "script";
  static final String SCRIPT_DEFAULT = "libexec/collector-script.sh";
  static final String FALLBACK_CONTACTS = PREFIX + "fallback_contacts";
  // Static configuration
  static final List<String> ATTRIBUTES_DOUBLE = Arrays.asList("cpu_load");
  static final List<String> ATTRIBUTES_LONG =
      Arrays.asList("free_disk", "total_disk", "free_ram", "total_ram", "free_swap", "total_swap",
          "num_processes", "num_cores", "logged_users");
  static final List<String> ATTRIBUTES_STRING = Arrays.asList("kernel_ver");
  static final List<String> ATTRIBUTES_SET_STRING = Arrays.asList("dns_names");
}
