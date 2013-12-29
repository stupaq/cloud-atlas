package stupaq.cloudatlas.gossiping;

public interface GossipingConfigKeys {
  // Runtime configuration section
  static final String PREFIX = "gossiping_internals.";
  // Runtime configuration entries
  static final String GOSSIP_RETRY_COUNT = "gossip_retry_count";
  static final int GOSSIP_RETRY_COUNT_DEFAULT = 3;
  static final String GOSSIP_RETRY_DELAY = "gossip_retry_delay";
  static final int GOSSIP_RETRY_DELAY_DEFAULT = 300;
  static final String EXPECTED_CONTACTS_MAX_COUNT = PREFIX + "expected_contacts_max_count";
  static final int EXPECTED_CONTACTS_MAX_COUNT_DEFAULT = 1000;
  static final String GOSSIP_ID_UNIQUENESS_INTERVAL = PREFIX + "gossip_id_uniqueness_interval";
  static final long GOSSIP_ID_UNIQUENESS_INTERVAL_DEFAULT = 1000L * 60;
  static final String FRAME_ASSEMBLING_TIMEOUT = "frame_assembling_timeout";
  static final long FRAME_ASSEMBLING_TIMEOUT_DEFAULT = 2000;
  static final String CONTACT_INFO_RETENTION = "contact_info_retention";
  static final long CONTACT_INFO_RETENTION_DEFAULT = 1000L * 60 * 10;
  // Static configuration
  static final int DATAGRAM_PACKET_MAX_SIZE = 512;
}
