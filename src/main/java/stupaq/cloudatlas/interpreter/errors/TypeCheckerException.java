package stupaq.cloudatlas.interpreter.errors;

public class TypeCheckerException extends RuntimeException {
  public TypeCheckerException(String message) {
    super(message);
  }

  public TypeCheckerException(Throwable t) {
    super(t);
  }
}
