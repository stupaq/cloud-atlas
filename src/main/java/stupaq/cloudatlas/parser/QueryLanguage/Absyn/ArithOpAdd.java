package stupaq.cloudatlas.parser.QueryLanguage.Absyn; // Java Package generated by the BNF Converter.

public class ArithOpAdd extends XArithOpAdd {

  public ArithOpAdd() { }

  public <R,A> R accept(stupaq.cloudatlas.parser.QueryLanguage.Absyn.XArithOpAdd.Visitor<R,A> v, A arg) { return v.visit(this, arg); }

  public boolean equals(Object o) {
    if (this == o) return true;
    if (o instanceof stupaq.cloudatlas.parser.QueryLanguage.Absyn.ArithOpAdd) {
      return true;
    }
    return false;
  }

  public int hashCode() {
    return 37;
  }


}
