package stupaq.cloudatlas.gossiping;

public interface GossipingInternalsConfigKeys {
  // Runtime configuration section
  static final String PREFIX = "gossiping_internals.";
  // Runtime configuration entries
  static final String CONTACTS_EXPECTED_MAX_COUNT = PREFIX + "contacts_expected_max_count";
  static final int CONTACTS_EXPECTED_MAX_COUNT_DEFAULT = 1000;
  static final String CONTACTS_INFO_RETENTION = PREFIX + "contacts_info_retention";
  static final long CONTACTS_INFO_RETENTION_DEFAULT = 1000L * 60 * 10;
  static final String CONTACT_FRESH_TIMEOUT = PREFIX + "contact_fresh_timeout";
  static final String GOSSIP_ID_UNIQUENESS_INTERVAL = PREFIX + "gossip_id_uniqueness_interval";
  static final long GOSSIP_ID_UNIQUENESS_INTERVAL_DEFAULT = 1000L * 60;
  static final String FRAME_ASSEMBLING_TIMEOUT = "frame_assembling_timeout";
  static final long FRAME_ASSEMBLING_TIMEOUT_DEFAULT = 2000;
  static final String GTP_PENDING_RESPONSE_RETENTION = "gtp_pending_response_retention";
  static final long GTP_PENDING_RESPONSE_RETENTION_DEFAULT = 1000L * 10;
  static final String GTP_OFFSET_RETENTION = "gtp_offset_retention";
  static final long GTP_OFFSET_RETENTION_DEFAULT = 1000L * 60 * 10;
  // Static configuration
  static final int DATAGRAM_PACKET_MAX_SIZE = 512;
  static final int LAST_GOSSIP_IN_SESSION_ID = 2;
}
