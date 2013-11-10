package stupaq.cloudatlas.interpreter.semantics;

public interface BinaryOperation<Arg0, Arg1, Result> {

  public Result apply(Arg0 arg0, Arg1 arg1);
}
