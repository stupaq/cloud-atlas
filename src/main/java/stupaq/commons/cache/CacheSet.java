package stupaq.commons.cache;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.ForwardingSet;

import java.util.Collection;
import java.util.Set;

import javax.annotation.Nonnull;

public class CacheSet<Element> extends ForwardingSet<Element> {
  private static final Object NOTHING = new Object();
  private final Cache<Element, Object> cache;

  public CacheSet(CacheBuilder<Object, Object> builder) {
    this.cache = builder.build();
  }

  @Override
  protected Set<Element> delegate() {
    return cache.asMap().keySet();
  }

  @Override
  public boolean addAll(@Nonnull Collection<? extends Element> collection) {
    return standardAddAll(collection);
  }

  @Override
  public boolean add(Element element) {
    if (!contains(element)) {
      cache.put(element, NOTHING);
      return true;
    }
    return false;
  }
}
