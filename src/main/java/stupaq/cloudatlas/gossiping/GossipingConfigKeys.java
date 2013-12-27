package stupaq.cloudatlas.gossiping;

public interface GossipingConfigKeys {
  // Runtime configuration section
  static final String PREFIX = "gossiping_internals.";
  // Runtime configuration entries
  static final String FRAME_ASSEMBLING_TIMEOUT = "frame_assembling_timeout";
  static final long FRAME_ASSEMBLING_TIMEOUT_DEFAULT = 2000;
  static final String GOSSIP_DUPLICATION_TIMEOUT = "gossip_duplication_timeout";
  static final long GOSSIP_DUPLICATION_TIMEOUT_DEFAULT = 1000L * 60 * 10;
  static final String CONTACT_INFO_RETENTION = "contact_info_retention";
  static final long CONTACT_INFO_RETENTION_DEFAULT = GOSSIP_DUPLICATION_TIMEOUT_DEFAULT;
  // Static configuration
}
