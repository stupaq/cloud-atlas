package stupaq.cloudatlas.parser.QueryLanguage;
import stupaq.cloudatlas.parser.QueryLanguage.Absyn.*;
/** BNFC-Generated Composition Visitor
*/

public class ComposVisitor<A> implements
  stupaq.cloudatlas.parser.QueryLanguage.Absyn.XProgram.Visitor<stupaq.cloudatlas.parser.QueryLanguage.Absyn.XProgram,A>,
  stupaq.cloudatlas.parser.QueryLanguage.Absyn.XStatement.Visitor<stupaq.cloudatlas.parser.QueryLanguage.Absyn.XStatement,A>,
  stupaq.cloudatlas.parser.QueryLanguage.Absyn.XWhereClause.Visitor<stupaq.cloudatlas.parser.QueryLanguage.Absyn.XWhereClause,A>,
  stupaq.cloudatlas.parser.QueryLanguage.Absyn.XOrderClause.Visitor<stupaq.cloudatlas.parser.QueryLanguage.Absyn.XOrderClause,A>,
  stupaq.cloudatlas.parser.QueryLanguage.Absyn.XOrderItem.Visitor<stupaq.cloudatlas.parser.QueryLanguage.Absyn.XOrderItem,A>,
  stupaq.cloudatlas.parser.QueryLanguage.Absyn.XOrderOption.Visitor<stupaq.cloudatlas.parser.QueryLanguage.Absyn.XOrderOption,A>,
  stupaq.cloudatlas.parser.QueryLanguage.Absyn.XNullsOption.Visitor<stupaq.cloudatlas.parser.QueryLanguage.Absyn.XNullsOption,A>,
  stupaq.cloudatlas.parser.QueryLanguage.Absyn.XSelectItem.Visitor<stupaq.cloudatlas.parser.QueryLanguage.Absyn.XSelectItem,A>,
  stupaq.cloudatlas.parser.QueryLanguage.Absyn.XExpression.Visitor<stupaq.cloudatlas.parser.QueryLanguage.Absyn.XExpression,A>,
  stupaq.cloudatlas.parser.QueryLanguage.Absyn.XArithOpAdd.Visitor<stupaq.cloudatlas.parser.QueryLanguage.Absyn.XArithOpAdd,A>,
  stupaq.cloudatlas.parser.QueryLanguage.Absyn.XArithOpMultiply.Visitor<stupaq.cloudatlas.parser.QueryLanguage.Absyn.XArithOpMultiply,A>,
  stupaq.cloudatlas.parser.QueryLanguage.Absyn.XRelOp.Visitor<stupaq.cloudatlas.parser.QueryLanguage.Absyn.XRelOp,A>,
  stupaq.cloudatlas.parser.QueryLanguage.Absyn.XBoolConst.Visitor<stupaq.cloudatlas.parser.QueryLanguage.Absyn.XBoolConst,A>,
  stupaq.cloudatlas.parser.QueryLanguage.Absyn.XExpressionNGT.Visitor<stupaq.cloudatlas.parser.QueryLanguage.Absyn.XExpressionNGT,A>,
  stupaq.cloudatlas.parser.QueryLanguage.Absyn.XRelOpNGT.Visitor<stupaq.cloudatlas.parser.QueryLanguage.Absyn.XRelOpNGT,A>
{
/* XProgram */
    public XProgram visit(stupaq.cloudatlas.parser.QueryLanguage.Absyn.Program p, A arg)
    {
      ListXStatement listxstatement_ = new ListXStatement();
      for (XStatement x : p.listxstatement_) {
        listxstatement_.add(x.accept(this,arg));
      }

      return new stupaq.cloudatlas.parser.QueryLanguage.Absyn.Program(listxstatement_);
    }

/* XStatement */
    public XStatement visit(stupaq.cloudatlas.parser.QueryLanguage.Absyn.Statement p, A arg)
    {
      ListXSelectItem listxselectitem_ = new ListXSelectItem();
      for (XSelectItem x : p.listxselectitem_) {
        listxselectitem_.add(x.accept(this,arg));
      }
      XWhereClause xwhereclause_ = p.xwhereclause_.accept(this, arg);
      XOrderClause xorderclause_ = p.xorderclause_.accept(this, arg);

      return new stupaq.cloudatlas.parser.QueryLanguage.Absyn.Statement(listxselectitem_, xwhereclause_, xorderclause_);
    }

/* XWhereClause */
    public XWhereClause visit(stupaq.cloudatlas.parser.QueryLanguage.Absyn.WhereClause p, A arg)
    {
      XExpression xexpression_ = p.xexpression_.accept(this, arg);

      return new stupaq.cloudatlas.parser.QueryLanguage.Absyn.WhereClause(xexpression_);
    }
    public XWhereClause visit(stupaq.cloudatlas.parser.QueryLanguage.Absyn.WhereClauseEmpty p, A arg)
    {

      return new stupaq.cloudatlas.parser.QueryLanguage.Absyn.WhereClauseEmpty();
    }

/* XOrderClause */
    public XOrderClause visit(stupaq.cloudatlas.parser.QueryLanguage.Absyn.OrderClause p, A arg)
    {
      ListXOrderItem listxorderitem_ = new ListXOrderItem();
      for (XOrderItem x : p.listxorderitem_) {
        listxorderitem_.add(x.accept(this,arg));
      }

      return new stupaq.cloudatlas.parser.QueryLanguage.Absyn.OrderClause(listxorderitem_);
    }
    public XOrderClause visit(stupaq.cloudatlas.parser.QueryLanguage.Absyn.OrderClauseEmpty p, A arg)
    {

      return new stupaq.cloudatlas.parser.QueryLanguage.Absyn.OrderClauseEmpty();
    }

/* XOrderItem */
    public XOrderItem visit(stupaq.cloudatlas.parser.QueryLanguage.Absyn.OrderItemCond p, A arg)
    {
      XExpression xexpression_ = p.xexpression_.accept(this, arg);
      XOrderOption xorderoption_ = p.xorderoption_.accept(this, arg);
      XNullsOption xnullsoption_ = p.xnullsoption_.accept(this, arg);

      return new stupaq.cloudatlas.parser.QueryLanguage.Absyn.OrderItemCond(xexpression_, xorderoption_, xnullsoption_);
    }

/* XOrderOption */
    public XOrderOption visit(stupaq.cloudatlas.parser.QueryLanguage.Absyn.OrderOptionAsc p, A arg)
    {

      return new stupaq.cloudatlas.parser.QueryLanguage.Absyn.OrderOptionAsc();
    }
    public XOrderOption visit(stupaq.cloudatlas.parser.QueryLanguage.Absyn.OrderOptionDesc p, A arg)
    {

      return new stupaq.cloudatlas.parser.QueryLanguage.Absyn.OrderOptionDesc();
    }
    public XOrderOption visit(stupaq.cloudatlas.parser.QueryLanguage.Absyn.OrderOptionEmpty p, A arg)
    {

      return new stupaq.cloudatlas.parser.QueryLanguage.Absyn.OrderOptionEmpty();
    }

/* XNullsOption */
    public XNullsOption visit(stupaq.cloudatlas.parser.QueryLanguage.Absyn.NullsOptionFirst p, A arg)
    {

      return new stupaq.cloudatlas.parser.QueryLanguage.Absyn.NullsOptionFirst();
    }
    public XNullsOption visit(stupaq.cloudatlas.parser.QueryLanguage.Absyn.NullsOptionLast p, A arg)
    {

      return new stupaq.cloudatlas.parser.QueryLanguage.Absyn.NullsOptionLast();
    }
    public XNullsOption visit(stupaq.cloudatlas.parser.QueryLanguage.Absyn.NullsOptionEmpty p, A arg)
    {

      return new stupaq.cloudatlas.parser.QueryLanguage.Absyn.NullsOptionEmpty();
    }

/* XSelectItem */
    public XSelectItem visit(stupaq.cloudatlas.parser.QueryLanguage.Absyn.SelectItem p, A arg)
    {
      XExpression xexpression_ = p.xexpression_.accept(this, arg);

      return new stupaq.cloudatlas.parser.QueryLanguage.Absyn.SelectItem(xexpression_);
    }
    public XSelectItem visit(stupaq.cloudatlas.parser.QueryLanguage.Absyn.SelectItemAs p, A arg)
    {
      XExpression xexpression_ = p.xexpression_.accept(this, arg);
      String xident_ = p.xident_;

      return new stupaq.cloudatlas.parser.QueryLanguage.Absyn.SelectItemAs(xexpression_, xident_);
    }

/* XExpression */
    public XExpression visit(stupaq.cloudatlas.parser.QueryLanguage.Absyn.CondExprOr p, A arg)
    {
      XExpression xexpression_1 = p.xexpression_1.accept(this, arg);
      XExpression xexpression_2 = p.xexpression_2.accept(this, arg);

      return new stupaq.cloudatlas.parser.QueryLanguage.Absyn.CondExprOr(xexpression_1, xexpression_2);
    }
    public XExpression visit(stupaq.cloudatlas.parser.QueryLanguage.Absyn.CondExprAnd p, A arg)
    {
      XExpression xexpression_1 = p.xexpression_1.accept(this, arg);
      XExpression xexpression_2 = p.xexpression_2.accept(this, arg);

      return new stupaq.cloudatlas.parser.QueryLanguage.Absyn.CondExprAnd(xexpression_1, xexpression_2);
    }
    public XExpression visit(stupaq.cloudatlas.parser.QueryLanguage.Absyn.CondExprNot p, A arg)
    {
      XExpression xexpression_ = p.xexpression_.accept(this, arg);

      return new stupaq.cloudatlas.parser.QueryLanguage.Absyn.CondExprNot(xexpression_);
    }
    public XExpression visit(stupaq.cloudatlas.parser.QueryLanguage.Absyn.BoolExprRegex p, A arg)
    {
      XExpression xexpression_ = p.xexpression_.accept(this, arg);
      String string_ = p.string_;

      return new stupaq.cloudatlas.parser.QueryLanguage.Absyn.BoolExprRegex(xexpression_, string_);
    }
    public XExpression visit(stupaq.cloudatlas.parser.QueryLanguage.Absyn.BoolExprRel p, A arg)
    {
      XExpression xexpression_1 = p.xexpression_1.accept(this, arg);
      XRelOp xrelop_ = p.xrelop_.accept(this, arg);
      XExpression xexpression_2 = p.xexpression_2.accept(this, arg);

      return new stupaq.cloudatlas.parser.QueryLanguage.Absyn.BoolExprRel(xexpression_1, xrelop_, xexpression_2);
    }
    public XExpression visit(stupaq.cloudatlas.parser.QueryLanguage.Absyn.ArithExprAdd p, A arg)
    {
      XExpression xexpression_1 = p.xexpression_1.accept(this, arg);
      XArithOpAdd xarithopadd_ = p.xarithopadd_.accept(this, arg);
      XExpression xexpression_2 = p.xexpression_2.accept(this, arg);

      return new stupaq.cloudatlas.parser.QueryLanguage.Absyn.ArithExprAdd(xexpression_1, xarithopadd_, xexpression_2);
    }
    public XExpression visit(stupaq.cloudatlas.parser.QueryLanguage.Absyn.ArithExprMultiply p, A arg)
    {
      XExpression xexpression_1 = p.xexpression_1.accept(this, arg);
      XArithOpMultiply xarithopmultiply_ = p.xarithopmultiply_.accept(this, arg);
      XExpression xexpression_2 = p.xexpression_2.accept(this, arg);

      return new stupaq.cloudatlas.parser.QueryLanguage.Absyn.ArithExprMultiply(xexpression_1, xarithopmultiply_, xexpression_2);
    }
    public XExpression visit(stupaq.cloudatlas.parser.QueryLanguage.Absyn.ArithExprNeg p, A arg)
    {
      XExpression xexpression_ = p.xexpression_.accept(this, arg);

      return new stupaq.cloudatlas.parser.QueryLanguage.Absyn.ArithExprNeg(xexpression_);
    }
    public XExpression visit(stupaq.cloudatlas.parser.QueryLanguage.Absyn.BasicExprVar p, A arg)
    {
      String xident_ = p.xident_;

      return new stupaq.cloudatlas.parser.QueryLanguage.Absyn.BasicExprVar(xident_);
    }
    public XExpression visit(stupaq.cloudatlas.parser.QueryLanguage.Absyn.BasicExprCall p, A arg)
    {
      String xident_ = p.xident_;
      ListXExpression listxexpression_ = new ListXExpression();
      for (XExpression x : p.listxexpression_) {
        listxexpression_.add(x.accept(this,arg));
      }

      return new stupaq.cloudatlas.parser.QueryLanguage.Absyn.BasicExprCall(xident_, listxexpression_);
    }
    public XExpression visit(stupaq.cloudatlas.parser.QueryLanguage.Absyn.BasicExprString p, A arg)
    {
      String string_ = p.string_;

      return new stupaq.cloudatlas.parser.QueryLanguage.Absyn.BasicExprString(string_);
    }
    public XExpression visit(stupaq.cloudatlas.parser.QueryLanguage.Absyn.BasicExprTrue p, A arg)
    {
      XBoolConst xboolconst_ = p.xboolconst_.accept(this, arg);

      return new stupaq.cloudatlas.parser.QueryLanguage.Absyn.BasicExprTrue(xboolconst_);
    }
    public XExpression visit(stupaq.cloudatlas.parser.QueryLanguage.Absyn.BasicExprInt p, A arg)
    {
      Integer integer_ = p.integer_;

      return new stupaq.cloudatlas.parser.QueryLanguage.Absyn.BasicExprInt(integer_);
    }
    public XExpression visit(stupaq.cloudatlas.parser.QueryLanguage.Absyn.BasicExprDouble p, A arg)
    {
      Double double_ = p.double_;

      return new stupaq.cloudatlas.parser.QueryLanguage.Absyn.BasicExprDouble(double_);
    }
    public XExpression visit(stupaq.cloudatlas.parser.QueryLanguage.Absyn.BasicExprBraces p, A arg)
    {

      return new stupaq.cloudatlas.parser.QueryLanguage.Absyn.BasicExprBraces();
    }
    public XExpression visit(stupaq.cloudatlas.parser.QueryLanguage.Absyn.BasicExprBrackets p, A arg)
    {

      return new stupaq.cloudatlas.parser.QueryLanguage.Absyn.BasicExprBrackets();
    }
    public XExpression visit(stupaq.cloudatlas.parser.QueryLanguage.Absyn.BasicExprAngle p, A arg)
    {
      ListXExpressionNGT listxexpressionngt_ = new ListXExpressionNGT();
      for (XExpressionNGT x : p.listxexpressionngt_) {
        listxexpressionngt_.add(x.accept(this,arg));
      }

      return new stupaq.cloudatlas.parser.QueryLanguage.Absyn.BasicExprAngle(listxexpressionngt_);
    }
    public XExpression visit(stupaq.cloudatlas.parser.QueryLanguage.Absyn.BasicExprStmt p, A arg)
    {
      XStatement xstatement_ = p.xstatement_.accept(this, arg);

      return new stupaq.cloudatlas.parser.QueryLanguage.Absyn.BasicExprStmt(xstatement_);
    }

/* XArithOpAdd */
    public XArithOpAdd visit(stupaq.cloudatlas.parser.QueryLanguage.Absyn.ArithOpAdd p, A arg)
    {

      return new stupaq.cloudatlas.parser.QueryLanguage.Absyn.ArithOpAdd();
    }
    public XArithOpAdd visit(stupaq.cloudatlas.parser.QueryLanguage.Absyn.ArithOpSubstract p, A arg)
    {

      return new stupaq.cloudatlas.parser.QueryLanguage.Absyn.ArithOpSubstract();
    }

/* XArithOpMultiply */
    public XArithOpMultiply visit(stupaq.cloudatlas.parser.QueryLanguage.Absyn.ArithOpMultiply p, A arg)
    {

      return new stupaq.cloudatlas.parser.QueryLanguage.Absyn.ArithOpMultiply();
    }
    public XArithOpMultiply visit(stupaq.cloudatlas.parser.QueryLanguage.Absyn.ArithOpDivide p, A arg)
    {

      return new stupaq.cloudatlas.parser.QueryLanguage.Absyn.ArithOpDivide();
    }
    public XArithOpMultiply visit(stupaq.cloudatlas.parser.QueryLanguage.Absyn.ArithOpModulo p, A arg)
    {

      return new stupaq.cloudatlas.parser.QueryLanguage.Absyn.ArithOpModulo();
    }

/* XRelOp */
    public XRelOp visit(stupaq.cloudatlas.parser.QueryLanguage.Absyn.RelOpEqual p, A arg)
    {

      return new stupaq.cloudatlas.parser.QueryLanguage.Absyn.RelOpEqual();
    }
    public XRelOp visit(stupaq.cloudatlas.parser.QueryLanguage.Absyn.RelOpNotEqual p, A arg)
    {

      return new stupaq.cloudatlas.parser.QueryLanguage.Absyn.RelOpNotEqual();
    }
    public XRelOp visit(stupaq.cloudatlas.parser.QueryLanguage.Absyn.RelOpGreater p, A arg)
    {

      return new stupaq.cloudatlas.parser.QueryLanguage.Absyn.RelOpGreater();
    }
    public XRelOp visit(stupaq.cloudatlas.parser.QueryLanguage.Absyn.RelOpGreaterEqual p, A arg)
    {

      return new stupaq.cloudatlas.parser.QueryLanguage.Absyn.RelOpGreaterEqual();
    }
    public XRelOp visit(stupaq.cloudatlas.parser.QueryLanguage.Absyn.RelOpLesser p, A arg)
    {

      return new stupaq.cloudatlas.parser.QueryLanguage.Absyn.RelOpLesser();
    }
    public XRelOp visit(stupaq.cloudatlas.parser.QueryLanguage.Absyn.RelOpLesserEqual p, A arg)
    {

      return new stupaq.cloudatlas.parser.QueryLanguage.Absyn.RelOpLesserEqual();
    }

/* XBoolConst */
    public XBoolConst visit(stupaq.cloudatlas.parser.QueryLanguage.Absyn.BoolConstTrue p, A arg)
    {

      return new stupaq.cloudatlas.parser.QueryLanguage.Absyn.BoolConstTrue();
    }
    public XBoolConst visit(stupaq.cloudatlas.parser.QueryLanguage.Absyn.BoolConstFalse p, A arg)
    {

      return new stupaq.cloudatlas.parser.QueryLanguage.Absyn.BoolConstFalse();
    }

/* XExpressionNGT */
    public XExpressionNGT visit(stupaq.cloudatlas.parser.QueryLanguage.Absyn.CondExprOrNGT p, A arg)
    {
      XExpressionNGT xexpressionngt_1 = p.xexpressionngt_1.accept(this, arg);
      XExpressionNGT xexpressionngt_2 = p.xexpressionngt_2.accept(this, arg);

      return new stupaq.cloudatlas.parser.QueryLanguage.Absyn.CondExprOrNGT(xexpressionngt_1, xexpressionngt_2);
    }
    public XExpressionNGT visit(stupaq.cloudatlas.parser.QueryLanguage.Absyn.CondExprAndNGT p, A arg)
    {
      XExpressionNGT xexpressionngt_1 = p.xexpressionngt_1.accept(this, arg);
      XExpressionNGT xexpressionngt_2 = p.xexpressionngt_2.accept(this, arg);

      return new stupaq.cloudatlas.parser.QueryLanguage.Absyn.CondExprAndNGT(xexpressionngt_1, xexpressionngt_2);
    }
    public XExpressionNGT visit(stupaq.cloudatlas.parser.QueryLanguage.Absyn.CondExprNotNGT p, A arg)
    {
      XExpressionNGT xexpressionngt_ = p.xexpressionngt_.accept(this, arg);

      return new stupaq.cloudatlas.parser.QueryLanguage.Absyn.CondExprNotNGT(xexpressionngt_);
    }
    public XExpressionNGT visit(stupaq.cloudatlas.parser.QueryLanguage.Absyn.BoolExprRegexNGT p, A arg)
    {
      XExpressionNGT xexpressionngt_ = p.xexpressionngt_.accept(this, arg);
      String string_ = p.string_;

      return new stupaq.cloudatlas.parser.QueryLanguage.Absyn.BoolExprRegexNGT(xexpressionngt_, string_);
    }
    public XExpressionNGT visit(stupaq.cloudatlas.parser.QueryLanguage.Absyn.BoolExprRelNGT p, A arg)
    {
      XExpressionNGT xexpressionngt_ = p.xexpressionngt_.accept(this, arg);
      XRelOpNGT xrelopngt_ = p.xrelopngt_.accept(this, arg);
      XExpression xexpression_ = p.xexpression_.accept(this, arg);

      return new stupaq.cloudatlas.parser.QueryLanguage.Absyn.BoolExprRelNGT(xexpressionngt_, xrelopngt_, xexpression_);
    }
    public XExpressionNGT visit(stupaq.cloudatlas.parser.QueryLanguage.Absyn.ExpressionNGT p, A arg)
    {
      XExpression xexpression_ = p.xexpression_.accept(this, arg);

      return new stupaq.cloudatlas.parser.QueryLanguage.Absyn.ExpressionNGT(xexpression_);
    }

/* XRelOpNGT */
    public XRelOpNGT visit(stupaq.cloudatlas.parser.QueryLanguage.Absyn.RelOpEqualNGT p, A arg)
    {

      return new stupaq.cloudatlas.parser.QueryLanguage.Absyn.RelOpEqualNGT();
    }
    public XRelOpNGT visit(stupaq.cloudatlas.parser.QueryLanguage.Absyn.RelOpNotEqualNGT p, A arg)
    {

      return new stupaq.cloudatlas.parser.QueryLanguage.Absyn.RelOpNotEqualNGT();
    }
    public XRelOpNGT visit(stupaq.cloudatlas.parser.QueryLanguage.Absyn.RelOpGreaterEqualNGT p, A arg)
    {

      return new stupaq.cloudatlas.parser.QueryLanguage.Absyn.RelOpGreaterEqualNGT();
    }
    public XRelOpNGT visit(stupaq.cloudatlas.parser.QueryLanguage.Absyn.RelOpLesserNGT p, A arg)
    {

      return new stupaq.cloudatlas.parser.QueryLanguage.Absyn.RelOpLesserNGT();
    }
    public XRelOpNGT visit(stupaq.cloudatlas.parser.QueryLanguage.Absyn.RelOpLesserEqualNGT p, A arg)
    {

      return new stupaq.cloudatlas.parser.QueryLanguage.Absyn.RelOpLesserEqualNGT();
    }

}