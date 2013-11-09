package stupaq.cloudatlas.interpreter.errors;

public class ConversionException extends RuntimeException {
  public ConversionException(String msg) {
    super(msg);
  }

  public ConversionException(Exception e) {
    super(e);
  }
}
