package stupaq.cloudatlas.query.parser.QueryLanguage;
import stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.*;
/** BNFC-Generated Abstract Visitor */
public class AbstractVisitor<R,A> implements AllVisitor<R,A> {
/* XProgram */
    public R visit(stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.Program p, A arg) { return visitDefault(p, arg); }
    public R visitDefault(stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.XProgram p, A arg) {
      throw new IllegalArgumentException(this.getClass().getName() + ": " + p);
    }
/* XStatement */
    public R visit(stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.Statement p, A arg) { return visitDefault(p, arg); }
    public R visitDefault(stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.XStatement p, A arg) {
      throw new IllegalArgumentException(this.getClass().getName() + ": " + p);
    }
/* XWhereClause */
    public R visit(stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.WhereClause p, A arg) { return visitDefault(p, arg); }
    public R visit(stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.WhereClauseEmpty p, A arg) { return visitDefault(p, arg); }
    public R visitDefault(stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.XWhereClause p, A arg) {
      throw new IllegalArgumentException(this.getClass().getName() + ": " + p);
    }
/* XOrderClause */
    public R visit(stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.OrderClause p, A arg) { return visitDefault(p, arg); }
    public R visit(stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.OrderClauseEmpty p, A arg) { return visitDefault(p, arg); }
    public R visitDefault(stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.XOrderClause p, A arg) {
      throw new IllegalArgumentException(this.getClass().getName() + ": " + p);
    }
/* XOrderItem */
    public R visit(stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.OrderItemCond p, A arg) { return visitDefault(p, arg); }
    public R visitDefault(stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.XOrderItem p, A arg) {
      throw new IllegalArgumentException(this.getClass().getName() + ": " + p);
    }
/* XOrderOption */
    public R visit(stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.OrderOptionAsc p, A arg) { return visitDefault(p, arg); }
    public R visit(stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.OrderOptionDesc p, A arg) { return visitDefault(p, arg); }
    public R visit(stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.OrderOptionEmpty p, A arg) { return visitDefault(p, arg); }
    public R visitDefault(stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.XOrderOption p, A arg) {
      throw new IllegalArgumentException(this.getClass().getName() + ": " + p);
    }
/* XNullsOption */
    public R visit(stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.NullsOptionFirst p, A arg) { return visitDefault(p, arg); }
    public R visit(stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.NullsOptionLast p, A arg) { return visitDefault(p, arg); }
    public R visit(stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.NullsOptionEmpty p, A arg) { return visitDefault(p, arg); }
    public R visitDefault(stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.XNullsOption p, A arg) {
      throw new IllegalArgumentException(this.getClass().getName() + ": " + p);
    }
/* XSelectItem */
    public R visit(stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.SelectItem p, A arg) { return visitDefault(p, arg); }
    public R visit(stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.SelectItemAs p, A arg) { return visitDefault(p, arg); }
    public R visitDefault(stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.XSelectItem p, A arg) {
      throw new IllegalArgumentException(this.getClass().getName() + ": " + p);
    }
/* XExpression */
    public R visit(stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.CondExprOr p, A arg) { return visitDefault(p, arg); }

    public R visit(stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.CondExprAnd p, A arg) { return visitDefault(p, arg); }

    public R visit(stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.CondExprNot p, A arg) { return visitDefault(p, arg); }

    public R visit(stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.BoolExprRegex p, A arg) { return visitDefault(p, arg); }

    public R visit(stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.BoolExprRel p, A arg) { return visitDefault(p, arg); }

    public R visit(stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.ArithExprAdd p, A arg) { return visitDefault(p, arg); }

    public R visit(stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.ArithExprMultiply p, A arg) { return visitDefault(p, arg); }

    public R visit(stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.ArithExprNeg p, A arg) { return visitDefault(p, arg); }

    public R visit(stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.BasicExprVar p, A arg) { return visitDefault(p, arg); }
    public R visit(stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.BasicExprCall p, A arg) { return visitDefault(p, arg); }
    public R visit(stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.BasicExprString p, A arg) { return visitDefault(p, arg); }
    public R visit(stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.BasicExprTrue p, A arg) { return visitDefault(p, arg); }
    public R visit(stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.BasicExprInt p, A arg) { return visitDefault(p, arg); }
    public R visit(stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.BasicExprDouble p, A arg) { return visitDefault(p, arg); }
    public R visit(stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.BasicExprStmt p, A arg) { return visitDefault(p, arg); }

    public R visitDefault(stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.XExpression p, A arg) {
      throw new IllegalArgumentException(this.getClass().getName() + ": " + p);
    }
/* XArithOpAdd */
    public R visit(stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.ArithOpAdd p, A arg) { return visitDefault(p, arg); }
    public R visit(stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.ArithOpSubstract p, A arg) { return visitDefault(p, arg); }
    public R visitDefault(stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.XArithOpAdd p, A arg) {
      throw new IllegalArgumentException(this.getClass().getName() + ": " + p);
    }
/* XArithOpMultiply */
    public R visit(stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.ArithOpMultiply p, A arg) { return visitDefault(p, arg); }
    public R visit(stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.ArithOpDivide p, A arg) { return visitDefault(p, arg); }
    public R visit(stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.ArithOpModulo p, A arg) { return visitDefault(p, arg); }
    public R visitDefault(stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.XArithOpMultiply p, A arg) {
      throw new IllegalArgumentException(this.getClass().getName() + ": " + p);
    }
/* XRelOp */
    public R visit(stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.RelOpEqual p, A arg) { return visitDefault(p, arg); }
    public R visit(stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.RelOpNotEqual p, A arg) { return visitDefault(p, arg); }
    public R visit(stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.RelOpGreater p, A arg) { return visitDefault(p, arg); }
    public R visit(stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.RelOpGreaterEqual p, A arg) { return visitDefault(p, arg); }
    public R visit(stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.RelOpLesser p, A arg) { return visitDefault(p, arg); }
    public R visit(stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.RelOpLesserEqual p, A arg) { return visitDefault(p, arg); }
    public R visitDefault(stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.XRelOp p, A arg) {
      throw new IllegalArgumentException(this.getClass().getName() + ": " + p);
    }
/* XBoolConst */
    public R visit(stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.BoolConstTrue p, A arg) { return visitDefault(p, arg); }
    public R visit(stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.BoolConstFalse p, A arg) { return visitDefault(p, arg); }
    public R visitDefault(stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.XBoolConst p, A arg) {
      throw new IllegalArgumentException(this.getClass().getName() + ": " + p);
    }

}
