package stupaq.cloudatlas.gossiping.peerstate;

import com.google.common.base.Predicate;
import com.google.common.cache.CacheBuilder;

import java.util.concurrent.TimeUnit;

import stupaq.cloudatlas.configuration.CAConfiguration;
import stupaq.cloudatlas.gossiping.GossipingInternalsConfigKeys;
import stupaq.cloudatlas.gossiping.dataformat.GossipId;
import stupaq.commons.cache.CacheSet;

public class GossipIdDuplicate implements Predicate<GossipId>, GossipingInternalsConfigKeys {
  private final CacheSet<GossipId> seenGossips;

  public GossipIdDuplicate(CAConfiguration config) {
    seenGossips = new CacheSet<>(CacheBuilder.newBuilder()
        .expireAfterAccess(
            config.getLong(GOSSIP_ID_UNIQUENESS_INTERVAL, GOSSIP_ID_UNIQUENESS_INTERVAL_DEFAULT),
            TimeUnit.MILLISECONDS));
  }

  @Override
  public boolean apply(GossipId input) {
    try {
      return seenGossips.contains(input);
    } finally {
      seenGossips.add(input);
    }
  }
}
