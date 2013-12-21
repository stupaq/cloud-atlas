package stupaq.cloudatlas.services.installer;

public interface QueriesInstallerConfigKeys {
  // Runtime configuration section
  static final String PREFIX = "installer.";
  // Runtime configuration entries
  static final String QUERIES_FILE = PREFIX + "queries_file";
  // Static configuration
  static final String ZONES_KEY = ".zones";
  static final String QUERY_KEY = ".query";
  static final String REMOVE_KEY = ".remove";
}
