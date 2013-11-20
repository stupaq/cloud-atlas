package stupaq.cloudatlas.interpreter.values;

import java.util.Arrays;
import java.util.Iterator;

import stupaq.cloudatlas.attribute.AttributeValue;
import stupaq.guava.base.Function2;

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
      SemanticValue<Other> second, Function2<Type, Other, Result> operation) {
    return second.zipWith(this, operation);
  }

  @Override
  <Arg0 extends AttributeValue, Arg1 extends AttributeValue, Result extends AttributeValue> RCollection<Result> zipImplementation(
      Iterator<Arg0> it0, Iterator<Arg1> it1, Function2<Arg0, Arg1, Result> operation) {
    RCollection<Result> result = new RCollection<>();
    while (it0.hasNext() && it1.hasNext()) {
      result.add(operation.apply(it0.next(), it1.next()));
    }
    return result;
  }
}
