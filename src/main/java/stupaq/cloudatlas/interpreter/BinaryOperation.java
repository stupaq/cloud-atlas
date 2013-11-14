package stupaq.cloudatlas.interpreter;

import com.google.common.base.Optional;

public abstract class BinaryOperation<Arg0, Arg1, Result> {

  public abstract Result apply(Arg0 arg0, Arg1 arg1);

  public final Optional<Result> applyOptional(final Optional<Arg0> arg0,
      final Optional<Arg1> arg1) {
    return (arg0.isPresent() && arg1.isPresent()) ? Optional
        .fromNullable(apply(arg0.get(), arg1.get())) : Optional.<Result>absent();
  }
}
