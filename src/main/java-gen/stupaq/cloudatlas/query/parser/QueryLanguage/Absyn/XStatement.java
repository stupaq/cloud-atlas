package stupaq.cloudatlas.query.parser.QueryLanguage.Absyn; // Java Package generated by the BNF Converter.

public abstract class XStatement implements java.io.Serializable {
  public abstract <R,A> R accept(XStatement.Visitor<R,A> v, A arg);
  public interface Visitor <R,A> {
    public R visit(stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.Statement p, A arg);

  }

}
