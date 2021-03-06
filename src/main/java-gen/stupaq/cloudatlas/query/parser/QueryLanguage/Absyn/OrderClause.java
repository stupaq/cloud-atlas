package stupaq.cloudatlas.query.parser.QueryLanguage.Absyn; // Java Package generated by the BNF Converter.

public class OrderClause extends XOrderClause {
  public final ListXOrderItem listxorderitem_;

  public OrderClause(ListXOrderItem p1) { listxorderitem_ = p1; }

  public <R,A> R accept(stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.XOrderClause.Visitor<R,A> v, A arg) { return v.visit(this, arg); }

  public boolean equals(Object o) {
    if (this == o) return true;
    if (o instanceof stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.OrderClause) {
      stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.OrderClause x = (stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.OrderClause)o;
      return this.listxorderitem_.equals(x.listxorderitem_);
    }
    return false;
  }

  public int hashCode() {
    return this.listxorderitem_.hashCode();
  }


}
