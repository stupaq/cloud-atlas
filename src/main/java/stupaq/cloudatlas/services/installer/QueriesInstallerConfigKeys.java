package stupaq.cloudatlas.services.installer;

public interface QueriesInstallerConfigKeys {
  // Runtime configuration section
  static final String PREFIX = "queries_installer.";
  // Runtime configuration entries
  static final String QUERIES_FILE = PREFIX + "queries_file";
  static final String POLL_INTERVAL = PREFIX + "poll_interval";
  static final long POLL_INTERVAL_DEFAULT = 10 * 1000L;
  // Queries file keys
  static final String REPLACE_ALL = "replace_all";
  static final boolean REPLACE_ALL_DEFAULT = false;
  // Per query keys
  static final String QUERY_ZONES = ".zones";
  static final String QUERY_CODE = ".query";
  static final String QUERY_ENABLED = ".enabled";
  static final boolean QUERY_ENABLED_DEFAULT = true;
}
