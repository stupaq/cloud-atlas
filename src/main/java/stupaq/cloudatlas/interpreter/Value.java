package stupaq.cloudatlas.interpreter;

import stupaq.cloudatlas.interpreter.semantics.ConvertibleValue;
import stupaq.cloudatlas.interpreter.semantics.OperableValue;

public interface Value {

  public Class<? extends Value> getType();

  public ConvertibleValue to();

  public OperableValue operate();
}
