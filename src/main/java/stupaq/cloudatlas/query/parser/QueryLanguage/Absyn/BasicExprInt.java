package stupaq.cloudatlas.query.parser.QueryLanguage.Absyn; // Java Package generated by the BNF Converter.

public class BasicExprInt extends XExpression {
  public final Integer integer_;

  public BasicExprInt(Integer p1) { integer_ = p1; }

  public <R,A> R accept(stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.XExpression.Visitor<R,A> v, A arg) { return v.visit(this, arg); }

  public boolean equals(Object o) {
    if (this == o) return true;
    if (o instanceof stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.BasicExprInt) {
      stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.BasicExprInt x = (stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.BasicExprInt)o;
      return this.integer_.equals(x.integer_);
    }
    return false;
  }

  public int hashCode() {
    return this.integer_.hashCode();
  }


}