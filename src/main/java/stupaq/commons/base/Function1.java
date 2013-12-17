package stupaq.commons.base;

import com.google.common.base.Function;
import com.google.common.base.Optional;

public abstract class Function1<Arg0, Result> implements Function<Arg0, Result> {
  @Override
  public abstract Result apply(Arg0 arg0);

  public Optional<Result> applyOptional(Optional<Arg0> arg0) {
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
}
