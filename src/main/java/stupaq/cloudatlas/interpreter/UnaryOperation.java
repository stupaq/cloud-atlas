package stupaq.cloudatlas.interpreter;

import com.google.common.base.Function;
import com.google.common.base.Optional;

public abstract class UnaryOperation<Arg0, Result> implements Function<Arg0, Result> {

  public final Optional<Result> applyOptional(Optional<Arg0> arg0) {
    return arg0.transform(this);
  }

  public final UnaryOperation<Optional<Arg0>, Optional<Result>> liftOptional() {
    return new UnaryOperation<Optional<Arg0>, Optional<Result>>() {
      @Override
      public Optional<Result> apply(Optional<Arg0> arg0) {
        return UnaryOperation.this.applyOptional(arg0);
      }
    };
  }
}
