package stupaq.cloudatlas.services.busybody;

public interface BusybodyConfigKeys {
  // Runtime configuration section
  static final String PREFIX = "gossiping.";
  // Runtime configuration entries
  static final String BIND_PORT = PREFIX + "bind_port";
  static final String GOSSIP_PERIOD = PREFIX + "period";
  static final long GOSSIP_PERIOD_DEFAULT = 5 * 1000L;
  static final String GOSSIP_RETRY_COUNT = "retry_count";
  static final int GOSSIP_RETRY_COUNT_DEFAULT = 3;
  static final String GOSSIP_RETRY_DELAY = "retry_delay";
  static final int GOSSIP_RETRY_DELAY_DEFAULT = 300;
  // Static configuration
}
