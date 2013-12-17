package stupaq.commons.lang;

public final class Fluent {
  private Fluent() {
  }

  public static <Result, Throwable extends java.lang.Throwable> Result raise(Throwable throwable)
      throws Throwable {
    throw throwable;
  }

  public static <Result> Result raiseNPE() {
    throw new NullPointerException();
  }
}
