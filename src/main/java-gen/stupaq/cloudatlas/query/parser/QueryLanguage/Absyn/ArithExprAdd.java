package stupaq.cloudatlas.query.parser.QueryLanguage.Absyn; // Java Package generated by the BNF Converter.

public class ArithExprAdd extends XExpression {
  public final XExpression xexpression_1, xexpression_2;
  public final XArithOpAdd xarithopadd_;

  public ArithExprAdd(XExpression p1, XArithOpAdd p2, XExpression p3) { xexpression_1 = p1; xarithopadd_ = p2; xexpression_2 = p3; }

  public <R,A> R accept(stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.XExpression.Visitor<R,A> v, A arg) { return v.visit(this, arg); }

  public boolean equals(Object o) {
    if (this == o) return true;
    if (o instanceof stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.ArithExprAdd) {
      stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.ArithExprAdd x = (stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.ArithExprAdd)o;
      return this.xexpression_1.equals(x.xexpression_1) && this.xarithopadd_.equals(x.xarithopadd_) && this.xexpression_2.equals(x.xexpression_2);
    }
    return false;
  }

  public int hashCode() {
    return 37*(37*(this.xexpression_1.hashCode())+this.xarithopadd_.hashCode())+this.xexpression_2.hashCode();
  }


}
