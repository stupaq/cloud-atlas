package stupaq.guava.base;

import com.google.common.base.Function;
import com.google.common.base.Optional;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

public abstract class Function1<Arg0, Result> implements Function<Arg0, Result> {

  public final Optional<Result> applyOptional(Optional<Arg0> arg0) {
    return arg0.transform(this);
  }

  public final Function1<Optional<Arg0>, Optional<Result>> liftOptional() {
    return new Function1<Optional<Arg0>, Optional<Result>>() {
      @Override
      public Optional<Result> apply(Optional<Arg0> arg0) {
        return Function1.this.applyOptional(arg0);
      }
    };
  }

  @Documented
  @Retention(RetentionPolicy.RUNTIME)
  @Target({ElementType.TYPE})
  public static @interface Impure {
  }

  @Impure
  public static abstract class CountingFunction1<Arg0, Result> extends Function1<Arg0, Result> {
    private int iteration = 0;

    @Override
    public final Result apply(Arg0 arg0) {
      try {
        return apply(iteration, arg0);
      } finally {
        iteration++;
      }
    }

    public abstract Result apply(int iteration, Arg0 arg0);
  }
}
