package stupaq.cloudatlas.query.errors;

public abstract class InterpreterException extends RuntimeException {
  protected InterpreterException() {
  }

  protected InterpreterException(String message) {
    super(message);
  }

  protected InterpreterException(String message, Throwable cause) {
    super(message, cause);
  }

  protected InterpreterException(Throwable cause) {
    super(cause);
  }

  protected InterpreterException(String message, Throwable cause, boolean enableSuppression,
      boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }
}
