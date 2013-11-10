package stupaq.cloudatlas.interpreter.semantics;

import com.google.common.base.Function;

import stupaq.cloudatlas.interpreter.Value;
import stupaq.cloudatlas.interpreter.types.RCollection;
import stupaq.cloudatlas.interpreter.types.RList;
import stupaq.cloudatlas.interpreter.types.RSingle;

public interface SemanticValue {

  public SemanticValue map(Function<Value, Value> function);

  public SemanticValue zip(SemanticValue second, BinaryOperation<Value, Value, Value> operation);

  public <Type extends Value> SemanticValue zipWith(RCollection<Type> first,
      BinaryOperation<Value, Value, Value> operation);

  public <Type extends Value> SemanticValue zipWith(RList<Type> first,
      BinaryOperation<Value, Value, Value> operation);

  public <Type extends Value> SemanticValue zipWith(RSingle<Type> first,
      BinaryOperation<Value, Value, Value> operation);
}
