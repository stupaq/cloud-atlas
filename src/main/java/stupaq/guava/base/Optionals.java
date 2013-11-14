package stupaq.guava.base;

import com.google.common.base.Optional;
import com.google.common.collect.FluentIterable;

public class Optionals {
  private Optionals() {
  }

  public static <Type> FluentIterable<Type> presentInstances(Iterable<Optional<Type>> iterable) {
    return FluentIterable.from(Optional.<Type>presentInstances(iterable));
  }

  public static <Type> Function1<Type, Optional<Type>> optionalOf() {
    return new Function1<Type, Optional<Type>>() {
      @Override
      public Optional<Type> apply(Type type) {
        return Optional.of(type);
      }
    };
  }
}
