package stupaq.cloudatlas.interpreter.values;

import stupaq.cloudatlas.attribute.AttributeValue;
import stupaq.cloudatlas.attribute.types.CABoolean;
import stupaq.cloudatlas.interpreter.semantics.AggregatingValue;
import stupaq.guava.base.Function1;
import stupaq.guava.base.Function2;

public interface SemanticValue<Type extends AttributeValue> {

  public SemanticValue<CABoolean> isNull();

  public <Result extends AttributeValue> SemanticValue<Result> map(
      Function1<Type, Result> function);

  public <Other extends AttributeValue, Result extends AttributeValue> SemanticValue<Result> zip(
      SemanticValue<Other> second, Function2<Type, Other, Result> operation);

  public <Other extends AttributeValue, Result extends AttributeValue> SemanticValue<Result> zipWith(
      RCollection<Other> first, Function2<Other, Type, Result> operation);

  public <Other extends AttributeValue, Result extends AttributeValue> SemanticValue<Result> zipWith(
      RList<Other> first, Function2<Other, Type, Result> operation);

  public <Other extends AttributeValue, Result extends AttributeValue> SemanticValue<Result> zipWith(
      RSingle<Other> first, Function2<Other, Type, Result> operation);

  public RSingle<Type> getSingle() throws SemanticValueCastException;

  public AggregatingValue aggregate();

  public static class SemanticValueCastException extends Exception {
  }
}
