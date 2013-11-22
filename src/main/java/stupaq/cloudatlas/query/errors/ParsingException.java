package stupaq.cloudatlas.query.errors;

public class ParsingException extends InterpreterException {
  public ParsingException(Throwable t) {
    super(t);
  }

  public ParsingException(String s) {
    super(s);
  }
}
