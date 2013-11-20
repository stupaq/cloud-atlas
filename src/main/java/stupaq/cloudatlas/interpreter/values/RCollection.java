package stupaq.cloudatlas.interpreter.values;

import java.util.Collections;
import java.util.Iterator;

import javax.annotation.Nonnull;

import stupaq.cloudatlas.attribute.AttributeValue;
import stupaq.cloudatlas.interpreter.typecheck.TypeInfo;
import stupaq.guava.base.Function2;

public class RCollection<Type extends AttributeValue> extends AbstractAggregate<Type> {
  public RCollection(@Nonnull TypeInfo<Type> typeInfo) {
    super(Collections.<Type>emptyList(), typeInfo);
  }

  @Override
  protected <Result extends AttributeValue> RCollection<Result> emptyInstance(
      TypeInfo<Result> typeInfo) {
    return new RCollection<>(typeInfo);
  }

  @Override
  public <Other extends AttributeValue, Result extends AttributeValue> SemanticValue<Result> zip(
      SemanticValue<Other> second, Function2<Type, Other, Result> operation) {
    return second.zipWith(this, operation);
  }

  @Override
  <Arg0 extends AttributeValue, Arg1 extends AttributeValue, Result extends AttributeValue> RCollection<Result> zipImplementation(
      Iterator<Arg0> it0, Iterator<Arg1> it1, Function2<Arg0, Arg1, Result> operation,
      TypeInfo<Result> typeInfo) {
    RCollection<Result> result = emptyInstance(typeInfo);
    while (it0.hasNext() && it1.hasNext()) {
      result.add(operation.apply(it0.next(), it1.next()));
    }
    return result;
  }
}
