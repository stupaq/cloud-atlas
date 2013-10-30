package stupaq.cloudatlas.parser.QueryLanguage.Absyn; // Java Package generated by the BNF Converter.

public class CondExprAndNGT extends XExpressionNGT {
  public final XExpressionNGT xexpressionngt_1, xexpressionngt_2;

  public CondExprAndNGT(XExpressionNGT p1, XExpressionNGT p2) { xexpressionngt_1 = p1; xexpressionngt_2 = p2; }

  public <R,A> R accept(stupaq.cloudatlas.parser.QueryLanguage.Absyn.XExpressionNGT.Visitor<R,A> v, A arg) { return v.visit(this, arg); }

  public boolean equals(Object o) {
    if (this == o) return true;
    if (o instanceof stupaq.cloudatlas.parser.QueryLanguage.Absyn.CondExprAndNGT) {
      stupaq.cloudatlas.parser.QueryLanguage.Absyn.CondExprAndNGT x = (stupaq.cloudatlas.parser.QueryLanguage.Absyn.CondExprAndNGT)o;
      return this.xexpressionngt_1.equals(x.xexpressionngt_1) && this.xexpressionngt_2.equals(x.xexpressionngt_2);
    }
    return false;
  }

  public int hashCode() {
    return 37*(this.xexpressionngt_1.hashCode())+this.xexpressionngt_2.hashCode();
  }


}