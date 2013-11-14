package stupaq.cloudatlas.interpreter.types;

import com.google.common.base.Optional;

import java.util.Arrays;
import java.util.Iterator;

import stupaq.cloudatlas.attribute.AttributeValue;
import stupaq.cloudatlas.interpreter.BinaryOperation;
import stupaq.cloudatlas.interpreter.semantics.SemanticValue;

public class RCollection<Type extends AttributeValue> extends AbstractAggregate<Type> {
  @SafeVarargs
  public RCollection(Type... elements) {
    super(Arrays.asList(elements));
  }

  @Override
  protected <Result extends AttributeValue> AbstractAggregate<Result> emptyInstance() {
    return new RCollection<>();
  }

  @Override
  public <Other extends AttributeValue, Result extends AttributeValue> SemanticValue<Result> zip(
      SemanticValue<Other> second, BinaryOperation<Type, Other, Result> operation) {
    return second.zipWith(this, operation);
  }

  @Override
  <Arg0 extends AttributeValue, Arg1 extends AttributeValue, Result extends AttributeValue> RCollection<Result> zipImplementation(
      Iterator<Optional<Arg0>> it0, Iterator<Optional<Arg1>> it1,
      BinaryOperation<Arg0, Arg1, Result> operation) {
    RCollection<Result> result = new RCollection<>();
    while (it0.hasNext() && it1.hasNext()) {
      result.add(operation.applyOptional(it0.next(), it1.next()));
    }
    return result;
  }
}