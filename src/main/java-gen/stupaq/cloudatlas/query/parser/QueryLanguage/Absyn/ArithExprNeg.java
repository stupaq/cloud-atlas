package stupaq.cloudatlas.query.parser.QueryLanguage.Absyn; // Java Package generated by the BNF Converter.

public class ArithExprNeg extends XExpression {
  public final XExpression xexpression_;

  public ArithExprNeg(XExpression p1) { xexpression_ = p1; }

  public <R,A> R accept(stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.XExpression.Visitor<R,A> v, A arg) { return v.visit(this, arg); }

  public boolean equals(Object o) {
    if (this == o) return true;
    if (o instanceof stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.ArithExprNeg) {
      stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.ArithExprNeg x = (stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.ArithExprNeg)o;
      return this.xexpression_.equals(x.xexpression_);
    }
    return false;
  }

  public int hashCode() {
    return this.xexpression_.hashCode();
  }


}