package stupaq.commons.collect;

import com.google.common.base.Preconditions;
import com.google.common.collect.FluentIterable;

import java.util.Collection;
import java.util.Random;

public final class Collections3 {
  private Collections3() {
  }

  public static <Element> Element random(Collection<Element> collection) {
    return random(FluentIterable.from(collection));
  }

  public static <Element> Element random(FluentIterable<Element> collection) {
    return random(collection, new Random());
  }

  public static <Element> Element random(FluentIterable<Element> iterable, Random random) {
    Preconditions.checkArgument(!iterable.isEmpty());
    return iterable.get(random.nextInt(iterable.size()));
  }
}
