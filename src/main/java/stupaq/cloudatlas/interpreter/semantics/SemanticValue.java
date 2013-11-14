package stupaq.cloudatlas.interpreter.semantics;

import stupaq.cloudatlas.attribute.AttributeValue;
import stupaq.guava.base.Function2;
import stupaq.guava.base.Function1;
import stupaq.cloudatlas.interpreter.types.RCollection;
import stupaq.cloudatlas.interpreter.types.RList;
import stupaq.cloudatlas.interpreter.types.RSingle;

public interface SemanticValue<Type extends AttributeValue> {

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

  public AggregatingValue aggregate();
}
