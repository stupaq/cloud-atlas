package stupaq.cloudatlas.query.parser.QueryLanguage;

import stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.XExpression;
import stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.XOrderItem;
import stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.XSelectItem;
import stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.XStatement;

/** BNFC-Generated Fold Visitor */
public abstract class FoldVisitor<R,A> implements AllVisitor<R,A> {
    public abstract R leaf(A arg);
    public abstract R combine(R x, R y, A arg);

/* XProgram */
    public R visit(stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.Program p, A arg) {
      R r = leaf(arg);
      for (XStatement x : p.listxstatement_) {
        r = combine(x.accept(this,arg), r, arg);
      }
      return r;
    }

/* XStatement */
    public R visit(stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.Statement p, A arg) {
      R r = leaf(arg);
      for (XSelectItem x : p.listxselectitem_) {
        r = combine(x.accept(this,arg), r, arg);
      }
      r = combine(p.xwhereclause_.accept(this, arg), r, arg);
      r = combine(p.xorderclause_.accept(this, arg), r, arg);
      return r;
    }

/* XWhereClause */
    public R visit(stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.WhereClause p, A arg) {
      R r = leaf(arg);
      r = combine(p.xexpression_.accept(this, arg), r, arg);
      return r;
    }
    public R visit(stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.WhereClauseEmpty p, A arg) {
      R r = leaf(arg);
      return r;
    }

/* XOrderClause */
    public R visit(stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.OrderClause p, A arg) {
      R r = leaf(arg);
      for (XOrderItem x : p.listxorderitem_) {
        r = combine(x.accept(this,arg), r, arg);
      }
      return r;
    }
    public R visit(stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.OrderClauseEmpty p, A arg) {
      R r = leaf(arg);
      return r;
    }

/* XOrderItem */
    public R visit(stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.OrderItemCond p, A arg) {
      R r = leaf(arg);
      r = combine(p.xexpression_.accept(this, arg), r, arg);
      r = combine(p.xorderoption_.accept(this, arg), r, arg);
      r = combine(p.xnullsoption_.accept(this, arg), r, arg);
      return r;
    }

/* XOrderOption */
    public R visit(stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.OrderOptionAsc p, A arg) {
      R r = leaf(arg);
      return r;
    }
    public R visit(stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.OrderOptionDesc p, A arg) {
      R r = leaf(arg);
      return r;
    }
    public R visit(stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.OrderOptionEmpty p, A arg) {
      R r = leaf(arg);
      return r;
    }

/* XNullsOption */
    public R visit(stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.NullsOptionFirst p, A arg) {
      R r = leaf(arg);
      return r;
    }
    public R visit(stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.NullsOptionLast p, A arg) {
      R r = leaf(arg);
      return r;
    }
    public R visit(stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.NullsOptionEmpty p, A arg) {
      R r = leaf(arg);
      return r;
    }

/* XSelectItem */
    public R visit(stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.SelectItem p, A arg) {
      R r = leaf(arg);
      r = combine(p.xexpression_.accept(this, arg), r, arg);
      return r;
    }
    public R visit(stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.SelectItemAs p, A arg) {
      R r = leaf(arg);
      r = combine(p.xexpression_.accept(this, arg), r, arg);
      return r;
    }

/* XExpression */
    public R visit(stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.CondExprOr p, A arg) {
      R r = leaf(arg);
      r = combine(p.xexpression_1.accept(this, arg), r, arg);
      r = combine(p.xexpression_2.accept(this, arg), r, arg);
      return r;
    }
    public R visit(stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.CondExprAnd p, A arg) {
      R r = leaf(arg);
      r = combine(p.xexpression_1.accept(this, arg), r, arg);
      r = combine(p.xexpression_2.accept(this, arg), r, arg);
      return r;
    }
    public R visit(stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.CondExprNot p, A arg) {
      R r = leaf(arg);
      r = combine(p.xexpression_.accept(this, arg), r, arg);
      return r;
    }
    public R visit(stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.BoolExprRegex p, A arg) {
      R r = leaf(arg);
      r = combine(p.xexpression_.accept(this, arg), r, arg);
      return r;
    }
    public R visit(stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.BoolExprRel p, A arg) {
      R r = leaf(arg);
      r = combine(p.xexpression_1.accept(this, arg), r, arg);
      r = combine(p.xrelop_.accept(this, arg), r, arg);
      r = combine(p.xexpression_2.accept(this, arg), r, arg);
      return r;
    }
    public R visit(stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.ArithExprAdd p, A arg) {
      R r = leaf(arg);
      r = combine(p.xexpression_1.accept(this, arg), r, arg);
      r = combine(p.xarithopadd_.accept(this, arg), r, arg);
      r = combine(p.xexpression_2.accept(this, arg), r, arg);
      return r;
    }
    public R visit(stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.ArithExprMultiply p, A arg) {
      R r = leaf(arg);
      r = combine(p.xexpression_1.accept(this, arg), r, arg);
      r = combine(p.xarithopmultiply_.accept(this, arg), r, arg);
      r = combine(p.xexpression_2.accept(this, arg), r, arg);
      return r;
    }
    public R visit(stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.ArithExprNeg p, A arg) {
      R r = leaf(arg);
      r = combine(p.xexpression_.accept(this, arg), r, arg);
      return r;
    }
    public R visit(stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.BasicExprVar p, A arg) {
      R r = leaf(arg);
      return r;
    }
    public R visit(stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.BasicExprCall p, A arg) {
      R r = leaf(arg);
      for (XExpression x : p.listxexpression_) {
        r = combine(x.accept(this,arg), r, arg);
      }
      return r;
    }
    public R visit(stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.BasicExprString p, A arg) {
      R r = leaf(arg);
      return r;
    }
    public R visit(stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.BasicExprTrue p, A arg) {
      R r = leaf(arg);
      r = combine(p.xboolconst_.accept(this, arg), r, arg);
      return r;
    }
    public R visit(stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.BasicExprInt p, A arg) {
      R r = leaf(arg);
      return r;
    }
    public R visit(stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.BasicExprDouble p, A arg) {
      R r = leaf(arg);
      return r;
    }
    public R visit(stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.BasicExprStmt p, A arg) {
      R r = leaf(arg);
      r = combine(p.xstatement_.accept(this, arg), r, arg);
      return r;
    }

/* XArithOpAdd */
    public R visit(stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.ArithOpAdd p, A arg) {
      R r = leaf(arg);
      return r;
    }
    public R visit(stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.ArithOpSubstract p, A arg) {
      R r = leaf(arg);
      return r;
    }

/* XArithOpMultiply */
    public R visit(stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.ArithOpMultiply p, A arg) {
      R r = leaf(arg);
      return r;
    }
    public R visit(stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.ArithOpDivide p, A arg) {
      R r = leaf(arg);
      return r;
    }
    public R visit(stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.ArithOpModulo p, A arg) {
      R r = leaf(arg);
      return r;
    }

/* XRelOp */
    public R visit(stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.RelOpEqual p, A arg) {
      R r = leaf(arg);
      return r;
    }
    public R visit(stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.RelOpNotEqual p, A arg) {
      R r = leaf(arg);
      return r;
    }
    public R visit(stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.RelOpGreater p, A arg) {
      R r = leaf(arg);
      return r;
    }
    public R visit(stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.RelOpGreaterEqual p, A arg) {
      R r = leaf(arg);
      return r;
    }
    public R visit(stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.RelOpLesser p, A arg) {
      R r = leaf(arg);
      return r;
    }
    public R visit(stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.RelOpLesserEqual p, A arg) {
      R r = leaf(arg);
      return r;
    }

/* XBoolConst */
    public R visit(stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.BoolConstTrue p, A arg) {
      R r = leaf(arg);
      return r;
    }
    public R visit(stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.BoolConstFalse p, A arg) {
      R r = leaf(arg);
      return r;
    }


}
