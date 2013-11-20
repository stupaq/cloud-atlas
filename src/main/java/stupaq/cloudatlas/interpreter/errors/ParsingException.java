package stupaq.cloudatlas.interpreter.errors;

public class ParsingException extends InterpreterException {
  public ParsingException(Throwable t) {
    super(t);
  }

  public ParsingException(String s) {
    super(s);
  }
}
