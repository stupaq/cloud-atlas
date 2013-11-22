package stupaq.cloudatlas.query.errors;

public class ConversionException extends InterpreterException {
  public ConversionException(String msg) {
    super(msg);
  }

  public ConversionException(Exception e) {
    super(e);
  }
}
