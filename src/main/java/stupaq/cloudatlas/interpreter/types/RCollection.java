package stupaq.cloudatlas.interpreter.types;

import stupaq.cloudatlas.interpreter.Value;
import stupaq.cloudatlas.interpreter.semantics.ConvertibleValue;
import stupaq.cloudatlas.interpreter.semantics.OperableValue;

// FIXME this can be either column or processed column (of different size)
public class RCollection implements Value {
  @Override
  public Class<RCollection> getType() {
    return RCollection.class;
  }

  @Override
  public ConvertibleValue to() {
    return null; // FIXME
  }

  @Override
  public OperableValue operate() {
    return null; // FIXME
  }
}
