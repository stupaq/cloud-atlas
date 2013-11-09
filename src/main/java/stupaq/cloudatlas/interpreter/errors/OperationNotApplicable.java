package stupaq.cloudatlas.interpreter.errors;

public class OperationNotApplicable extends RuntimeException {
  public OperationNotApplicable(String message) {
    super(message);
  }

  public OperationNotApplicable(Exception cause) {
    super(cause);
  }
}
