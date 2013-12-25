package stupaq.commons.collect;

import com.google.common.base.Preconditions;

import java.util.Collection;
import java.util.Random;

public class Collections3 {
  public static <Element> Element random(Collection<Element> collection) {
    return random(collection, new Random());
  }

  public static <Element> Element random(Collection<Element> collection, Random random) {
    Preconditions.checkArgument(!collection.isEmpty());
    int index = random.nextInt(collection.size());
    for (Element element : collection) {
      if (index-- == 0) {
        return element;
      }
    }
    return null;
  }
}
