package stupaq.cloudatlas.interpreter;

import stupaq.cloudatlas.interpreter.semantics.ConvertibleValue;
import stupaq.cloudatlas.interpreter.semantics.OperableValue;
import stupaq.cloudatlas.interpreter.semantics.RelationalValue;

public interface Value extends Comparable<Value> {

  public Class<? extends Value> getType();

  public ConvertibleValue to();

  public OperableValue op();

  public RelationalValue rel();
}
