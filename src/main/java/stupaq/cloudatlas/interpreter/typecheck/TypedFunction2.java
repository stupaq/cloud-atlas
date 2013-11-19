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
import stupaq.guava.base.Function2;

public abstract class TypedFunction2<Arg0 extends AttributeValue, Arg1 extends AttributeValue, Result extends AttributeValue>
    extends Function2<Arg0, Arg1, Result> {
  private final TypeInfo<Arg0> arg0Type;
  private final TypeInfo<Arg1> arg1Type;

  public TypedFunction2(@Nonnull TypeInfo<Arg0> arg0Type, @Nonnull TypeInfo<Arg1> arg1Type) {
    Preconditions.checkNotNull(arg0Type);
    Preconditions.checkNotNull(arg1Type);
    this.arg0Type = arg0Type;
    this.arg1Type = arg1Type;
  }

  public static <Arg0 extends AttributeValue, Arg1 extends AttributeValue, Result extends AttributeValue> TypedFunction2<Arg0, Arg1, Result> delegate(
      final Function2<Arg0, Arg1, Result> delegate, TypeInfo<Arg0> arg0Type,
      final TypeInfo<Arg1> arg1Type) {
    return new TypedFunction2<Arg0, Arg1, Result>(arg0Type, arg1Type) {
      @Override
      public Result apply(Arg0 arg0, Arg1 arg1) {
        return delegate.apply(arg0, arg1);
      }
    };
  }

  @Override
  public Optional<Result> applyOptional(Optional<Arg0> arg0, Optional<Arg1> arg1) {
    // Type checking will be performed iff no real computation can be performed
    if (!arg0.isPresent() || !arg1.isPresent()) {
      try {
        this.apply(arg0Type.getRepresentative(), arg1Type.getRepresentative());
      } catch (ConversionException | EvaluationException | ParsingException | UndefinedOperationException e) {
        throw new TypeCheckerException(e);
        // We let go to any TypeCheckerException so that we will not create exceptions chain
      }
      // Discard result
      return Optional.absent();
    } else {
      // Actual execution
      return super.applyOptional(arg0, arg1);
    }
  }
}
