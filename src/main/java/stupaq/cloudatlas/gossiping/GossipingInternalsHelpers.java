package stupaq.cloudatlas.gossiping;

import com.google.common.cache.CacheBuilder;

import java.util.concurrent.TimeUnit;

import stupaq.cloudatlas.configuration.CAConfiguration;

public final class GossipingInternalsHelpers implements GossipingInternalsConfigKeys {
  private GossipingInternalsHelpers() {
  }

  public static CacheBuilder<Object, Object> contactsInfoCache(CAConfiguration config) {
    return CacheBuilder.newBuilder()
        .maximumSize(
            config.getInt(CONTACTS_EXPECTED_MAX_COUNT, CONTACTS_EXPECTED_MAX_COUNT_DEFAULT))
        .expireAfterAccess(config.getLong(CONTACTS_INFO_RETENTION, CONTACTS_INFO_RETENTION_DEFAULT),
            TimeUnit.MILLISECONDS);
  }
}
