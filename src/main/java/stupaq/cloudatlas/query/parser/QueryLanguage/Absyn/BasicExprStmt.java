package stupaq.cloudatlas.query.parser.QueryLanguage.Absyn; // Java Package generated by the BNF Converter.

public class BasicExprStmt extends XExpression {
  public final XStatement xstatement_;

  public BasicExprStmt(XStatement p1) { xstatement_ = p1; }

  public <R,A> R accept(stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.XExpression.Visitor<R,A> v, A arg) { return v.visit(this, arg); }

  public boolean equals(Object o) {
    if (this == o) return true;
    if (o instanceof stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.BasicExprStmt) {
      stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.BasicExprStmt x = (stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.BasicExprStmt)o;
      return this.xstatement_.equals(x.xstatement_);
    }
    return false;
  }

  public int hashCode() {
    return this.xstatement_.hashCode();
  }


}