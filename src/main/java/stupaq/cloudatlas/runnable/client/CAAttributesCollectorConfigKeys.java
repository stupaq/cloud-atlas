package stupaq.cloudatlas.runnable.client;

interface CAAttributesCollectorConfigKeys {
  static final String PREFIX = "collector";
  static final String PUSH_INTERVAL = PREFIX + ".push_interval";
  static final long PUSH_INTERVAL_DEFAULT = 10 * 1000L;
  static final String SCRIPT = PREFIX + ".script";
  static final String SCRIPT_DEFAULT = "libexec/collector-script.sh";
}
