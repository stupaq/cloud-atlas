package stupaq.cloudatlas.interpreter.semantics;

import stupaq.cloudatlas.attribute.AttributeValue;
import stupaq.cloudatlas.interpreter.BinaryOperation;
import stupaq.cloudatlas.interpreter.UnaryOperation;
import stupaq.cloudatlas.interpreter.types.RCollection;
import stupaq.cloudatlas.interpreter.types.RList;
import stupaq.cloudatlas.interpreter.types.RSingle;

public interface SemanticValue<Type extends AttributeValue> {

  public <Result extends AttributeValue> SemanticValue<Result> map(
      UnaryOperation<Type, Result> function);

  public <Other extends AttributeValue, Result extends AttributeValue> SemanticValue<Result> zip(
      SemanticValue<Other> second, BinaryOperation<Type, Other, Result> operation);

  public <Other extends AttributeValue, Result extends AttributeValue> SemanticValue<Result> zipWith(
      RCollection<Other> first, BinaryOperation<Other, Type, Result> operation);

  public <Other extends AttributeValue, Result extends AttributeValue> SemanticValue<Result> zipWith(
      RList<Other> first, BinaryOperation<Other, Type, Result> operation);

  public <Other extends AttributeValue, Result extends AttributeValue> SemanticValue<Result> zipWith(
      RSingle<Other> first, BinaryOperation<Other, Type, Result> operation);

  public AggregatingValue aggregate();
}
