package stupaq.cloudatlas.interpreter.evaluation.context;

public class Context {
  public final OutputContext output;
  public final InputContext input;

  public Context(OutputContext output, InputContext input) {
    this.output = output;
    this.input = input;
  }
}
