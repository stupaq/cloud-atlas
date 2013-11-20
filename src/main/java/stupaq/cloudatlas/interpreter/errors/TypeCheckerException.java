package stupaq.cloudatlas.interpreter.errors;

public class TypeCheckerException extends InterpreterException {
  public TypeCheckerException(String message) {
    super(message);
  }

  public TypeCheckerException(Exception e) {
    super(e);
  }
}
