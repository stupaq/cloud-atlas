package stupaq.cloudatlas.parser.QueryLanguage.Absyn; // Java Package generated by the BNF Converter.

public abstract class XSelectItem implements java.io.Serializable {
  public abstract <R,A> R accept(XSelectItem.Visitor<R,A> v, A arg);
  public interface Visitor <R,A> {
    public R visit(stupaq.cloudatlas.parser.QueryLanguage.Absyn.SelectItem p, A arg);
    public R visit(stupaq.cloudatlas.parser.QueryLanguage.Absyn.SelectItemAs p, A arg);

  }

}
