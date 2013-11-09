package stupaq.cloudatlas.interpreter;

public interface Value {

  public Class<? extends Value> getType();

  public ConvertibleValue to();
}
