package stupaq.cloudatlas.parser.QueryLanguage.Absyn; // Java Package generated by the BNF Converter.

public abstract class XExpression implements java.io.Serializable {
  public abstract <R,A> R accept(XExpression.Visitor<R,A> v, A arg);
  public interface Visitor <R,A> {
    public R visit(stupaq.cloudatlas.parser.QueryLanguage.Absyn.CondExprOr p, A arg);
    public R visit(stupaq.cloudatlas.parser.QueryLanguage.Absyn.CondExprAnd p, A arg);
    public R visit(stupaq.cloudatlas.parser.QueryLanguage.Absyn.CondExprNot p, A arg);
    public R visit(stupaq.cloudatlas.parser.QueryLanguage.Absyn.BoolExprRegex p, A arg);
    public R visit(stupaq.cloudatlas.parser.QueryLanguage.Absyn.BoolExprRel p, A arg);
    public R visit(stupaq.cloudatlas.parser.QueryLanguage.Absyn.ArithExprAdd p, A arg);
    public R visit(stupaq.cloudatlas.parser.QueryLanguage.Absyn.ArithExprMultiply p, A arg);
    public R visit(stupaq.cloudatlas.parser.QueryLanguage.Absyn.ArithExprNeg p, A arg);
    public R visit(stupaq.cloudatlas.parser.QueryLanguage.Absyn.BasicExprVar p, A arg);
    public R visit(stupaq.cloudatlas.parser.QueryLanguage.Absyn.BasicExprCall p, A arg);
    public R visit(stupaq.cloudatlas.parser.QueryLanguage.Absyn.BasicExprString p, A arg);
    public R visit(stupaq.cloudatlas.parser.QueryLanguage.Absyn.BasicExprTrue p, A arg);
    public R visit(stupaq.cloudatlas.parser.QueryLanguage.Absyn.BasicExprInt p, A arg);
    public R visit(stupaq.cloudatlas.parser.QueryLanguage.Absyn.BasicExprDouble p, A arg);
    public R visit(stupaq.cloudatlas.parser.QueryLanguage.Absyn.BasicExprBraces p, A arg);
    public R visit(stupaq.cloudatlas.parser.QueryLanguage.Absyn.BasicExprBrackets p, A arg);
    public R visit(stupaq.cloudatlas.parser.QueryLanguage.Absyn.BasicExprAngle p, A arg);
    public R visit(stupaq.cloudatlas.parser.QueryLanguage.Absyn.BasicExprStmt p, A arg);

  }

}