package stupaq.cloudatlas.services.busybody;

public interface BusybodyConfigKeys {
  // Runtime configuration section
  static final String PREFIX = "gossip.";
  // Runtime configuration entries
  static final String BIND_PORT = PREFIX + "bind_port";
  static final String GOSSIP_PERIOD = PREFIX + "gossip_period";
  static final long GOSSIP_PERIOD_DEFAULT = 5 * 1000L;
  static final String GOSSIP_TIMEOUT = PREFIX + "gossip_timeout";
  static final long GOSSIP_TIMEOUT_DEFAULT = GOSSIP_PERIOD_DEFAULT;
  int MESSAGE_SIZE_DEFAULT = 1024;
  // Static configuration
}
