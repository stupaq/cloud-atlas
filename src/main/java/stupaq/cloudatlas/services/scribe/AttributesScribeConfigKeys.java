package stupaq.cloudatlas.services.scribe;

public interface AttributesScribeConfigKeys {
  static final String PREFIX = "scribe.";
  static final String DIRECTORY = PREFIX + "directory";
  static final String DIRECTORY_DEFAULT = "scribe/";
  static final String ENTITIES = PREFIX + "entities";
  static final String FETCH_INTERVAL = PREFIX + "fetch_interval";
  static final long FETCH_INTERVAL_DEFAULT = 10 * 1000L;
}
