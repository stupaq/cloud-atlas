package stupaq.cloudatlas.parser.QueryLanguage.Absyn; // Java Package generated by the BNF Converter.

public class RelOpEqual extends XRelOp {

  public RelOpEqual() { }

  public <R,A> R accept(stupaq.cloudatlas.parser.QueryLanguage.Absyn.XRelOp.Visitor<R,A> v, A arg) { return v.visit(this, arg); }

  public boolean equals(Object o) {
    if (this == o) return true;
    if (o instanceof stupaq.cloudatlas.parser.QueryLanguage.Absyn.RelOpEqual) {
      return true;
    }
    return false;
  }

  public int hashCode() {
    return 37;
  }


}
