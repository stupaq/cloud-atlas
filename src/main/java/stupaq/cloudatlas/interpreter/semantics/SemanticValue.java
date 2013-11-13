package stupaq.cloudatlas.interpreter.semantics;

import com.google.common.base.Function;

import stupaq.cloudatlas.attribute.AttributeValue;
import stupaq.cloudatlas.interpreter.types.RCollection;
import stupaq.cloudatlas.interpreter.types.RList;
import stupaq.cloudatlas.interpreter.types.RSingle;

public interface SemanticValue {

  public SemanticValue map(Function<AttributeValue, AttributeValue> function);

  public SemanticValue zip(SemanticValue second,
      BinaryOperation<AttributeValue, AttributeValue, AttributeValue> operation);

  public <Type extends AttributeValue> SemanticValue zipWith(RCollection<Type> first,
      BinaryOperation<AttributeValue, AttributeValue, AttributeValue> operation);

  public <Type extends AttributeValue> SemanticValue zipWith(RList<Type> first,
      BinaryOperation<AttributeValue, AttributeValue, AttributeValue> operation);

  public <Type extends AttributeValue> SemanticValue zipWith(RSingle<Type> first,
      BinaryOperation<AttributeValue, AttributeValue, AttributeValue> operation);

  public AggregatingValue aggregate();
}
