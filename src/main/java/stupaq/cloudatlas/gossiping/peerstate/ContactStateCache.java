package stupaq.cloudatlas.gossiping.peerstate;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.ForwardingLoadingCache;
import com.google.common.cache.LoadingCache;

import java.util.concurrent.TimeUnit;

import stupaq.cloudatlas.attribute.values.CAContact;
import stupaq.cloudatlas.configuration.CAConfiguration;
import stupaq.cloudatlas.gossiping.GossipingConfigKeys;
import stupaq.commons.cache.ReferenceCountedRemovalListener;

public class ContactStateCache<State> extends ForwardingLoadingCache<CAContact, State>
    implements GossipingConfigKeys {
  private final LoadingCache<CAContact, State> cache;

  public ContactStateCache(CAConfiguration config, CacheLoader<CAContact, State> loader) {
    cache = CacheBuilder.newBuilder()
        .expireAfterAccess(config.getLong(CONTACT_INFO_RETENTION, CONTACT_INFO_RETENTION_DEFAULT),
            TimeUnit.MILLISECONDS)
        .removalListener(new ReferenceCountedRemovalListener())
        .build(loader);
  }

  @Override
  protected LoadingCache<CAContact, State> delegate() {
    return cache;
  }
}
