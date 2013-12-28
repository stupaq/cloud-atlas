package stupaq.cloudatlas.services.busybody;

public interface BusybodyConfigKeys {
  // Runtime configuration section
  static final String PREFIX = "gossiping.";
  // Runtime configuration entries
  static final String BIND_PORT = PREFIX + "bind_port";
  static final String GOSSIP_PERIOD = PREFIX + "gossip_period";
  static final long GOSSIP_PERIOD_DEFAULT = 5 * 1000L;
  // Static configuration
}
