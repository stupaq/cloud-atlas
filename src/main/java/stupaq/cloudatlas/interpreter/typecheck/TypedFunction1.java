package stupaq.cloudatlas.interpreter.typecheck;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;

import javax.annotation.Nonnull;

import stupaq.cloudatlas.attribute.AttributeValue;
import stupaq.cloudatlas.interpreter.errors.ConversionException;
import stupaq.cloudatlas.interpreter.errors.EvaluationException;
import stupaq.cloudatlas.interpreter.errors.ParsingException;
import stupaq.cloudatlas.interpreter.errors.TypeCheckerException;
import stupaq.cloudatlas.interpreter.errors.UndefinedOperationException;
import stupaq.guava.base.Function1;

public abstract class TypedFunction1<Arg0 extends AttributeValue, Result extends AttributeValue>
    extends Function1<Arg0, Result> {
  private final TypeInfo<Arg0> arg0Type;

  public TypedFunction1(@Nonnull TypeInfo<Arg0> arg0Type) {
    Preconditions.checkNotNull(arg0Type);
    this.arg0Type = arg0Type;
  }

  public static <Arg0 extends AttributeValue, Result extends AttributeValue> TypedFunction1<Arg0,
      Result> delegate(
      final Function1<Arg0, Result> delegate, TypeInfo<Arg0> arg0Type) {
    return new TypedFunction1<Arg0, Result>(arg0Type) {
      @Override
      public Result apply(Arg0 arg0) {
        return delegate.apply(arg0);
      }
    };
  }

  @Override
  public Optional<Result> applyOptional(Optional<Arg0> arg0) {
    // Type checking will be performed iff no real computation can be performed
    if (!arg0.isPresent()) {
      try {
        this.apply(arg0Type.getRepresentative());
      } catch (ConversionException | EvaluationException | ParsingException |
          UndefinedOperationException e) {
        throw new TypeCheckerException(e);
        // We let go to any TypeCheckerException so that we will not create exceptions chain
      }
      // Discard result
      return Optional.absent();
    } else {
      // Actual execution
      return super.applyOptional(arg0);
    }
  }
}
