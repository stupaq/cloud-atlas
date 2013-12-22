package stupaq.commons.base;

import com.google.common.base.Optional;

public abstract class Function2<Arg0, Arg1, Result> {

  public abstract Result apply(Arg0 arg0, Arg1 arg1);

  public final Function1<Arg1, Result> bind0(final Arg0 arg0) {
    return new Function1<Arg1, Result>() {
      @Override
      public Result apply(Arg1 arg1) {
        return Function2.this.apply(arg0, arg1);
      }
    };
  }

  public final Function2<Arg1, Arg0, Result> flip() {
    return new Function2<Arg1, Arg0, Result>() {
      @Override
      public Result apply(Arg1 arg1, Arg0 arg0) {
        return Function2.this.apply(arg0, arg1);
      }
    };
  }

  public Optional<Result> applyOptional(Optional<Arg0> arg0, Optional<Arg1> arg1) {
    return (arg0.isPresent() && arg1.isPresent()) ?
        Optional.fromNullable(apply(arg0.get(), arg1.get())) : Optional.<Result>absent();
  }

  public final Function2<Optional<Arg0>, Optional<Arg1>, Optional<Result>> liftOptional() {
    return new Function2<Optional<Arg0>, Optional<Arg1>, Optional<Result>>() {
      @Override
      public Optional<Result> apply(Optional<Arg0> arg0Optional, Optional<Arg1> arg1Optional) {
        return Function2.this.applyOptional(arg0Optional, arg1Optional);
      }
    };
  }
}
