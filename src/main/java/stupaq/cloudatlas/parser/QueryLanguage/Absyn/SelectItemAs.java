package stupaq.cloudatlas.parser.QueryLanguage.Absyn; // Java Package generated by the BNF Converter.

public class SelectItemAs extends XSelectItem {
  public final XExpression xexpression_;
  public final String xident_;

  public SelectItemAs(XExpression p1, String p2) { xexpression_ = p1; xident_ = p2; }

  public <R,A> R accept(stupaq.cloudatlas.parser.QueryLanguage.Absyn.XSelectItem.Visitor<R,A> v, A arg) { return v.visit(this, arg); }

  public boolean equals(Object o) {
    if (this == o) return true;
    if (o instanceof stupaq.cloudatlas.parser.QueryLanguage.Absyn.SelectItemAs) {
      stupaq.cloudatlas.parser.QueryLanguage.Absyn.SelectItemAs x = (stupaq.cloudatlas.parser.QueryLanguage.Absyn.SelectItemAs)o;
      return this.xexpression_.equals(x.xexpression_) && this.xident_.equals(x.xident_);
    }
    return false;
  }

  public int hashCode() {
    return 37*(this.xexpression_.hashCode())+this.xident_.hashCode();
  }


}
