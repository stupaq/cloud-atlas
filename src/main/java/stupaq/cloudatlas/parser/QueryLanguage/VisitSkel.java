package stupaq.cloudatlas.parser.QueryLanguage;
import stupaq.cloudatlas.parser.QueryLanguage.Absyn.*;
/*** BNFC-Generated Visitor Design Pattern Skeleton. ***/
/* This implements the common visitor design pattern.
   Tests show it to be slightly less efficient than the
   instanceof method, but easier to use. 
   Replace the R and A parameters with the desired return
   and context types.*/

public class VisitSkel
{
  public class XProgramVisitor<R,A> implements XProgram.Visitor<R,A>
  {
    public R visit(stupaq.cloudatlas.parser.QueryLanguage.Absyn.Program p, A arg)
    {
      /* Code For Program Goes Here */

      for (XStatement x : p.listxstatement_) {
      }

      return null;
    }

  }
  public class XStatementVisitor<R,A> implements XStatement.Visitor<R,A>
  {
    public R visit(stupaq.cloudatlas.parser.QueryLanguage.Absyn.Statement p, A arg)
    {
      /* Code For Statement Goes Here */

      for (XSelectItem x : p.listxselectitem_) {
      }
      p.xwhereclause_.accept(new XWhereClauseVisitor<R,A>(), arg);
      p.xorderclause_.accept(new XOrderClauseVisitor<R,A>(), arg);

      return null;
    }

  }
  public class XWhereClauseVisitor<R,A> implements XWhereClause.Visitor<R,A>
  {
    public R visit(stupaq.cloudatlas.parser.QueryLanguage.Absyn.WhereClause p, A arg)
    {
      /* Code For WhereClause Goes Here */

      p.xexpression_.accept(new XExpressionVisitor<R,A>(), arg);

      return null;
    }
    public R visit(stupaq.cloudatlas.parser.QueryLanguage.Absyn.WhereClauseEmpty p, A arg)
    {
      /* Code For WhereClauseEmpty Goes Here */


      return null;
    }

  }
  public class XOrderClauseVisitor<R,A> implements XOrderClause.Visitor<R,A>
  {
    public R visit(stupaq.cloudatlas.parser.QueryLanguage.Absyn.OrderClause p, A arg)
    {
      /* Code For OrderClause Goes Here */

      for (XOrderItem x : p.listxorderitem_) {
      }

      return null;
    }
    public R visit(stupaq.cloudatlas.parser.QueryLanguage.Absyn.OrderClauseEmpty p, A arg)
    {
      /* Code For OrderClauseEmpty Goes Here */


      return null;
    }

  }
  public class XOrderItemVisitor<R,A> implements XOrderItem.Visitor<R,A>
  {
    public R visit(stupaq.cloudatlas.parser.QueryLanguage.Absyn.OrderItemCond p, A arg)
    {
      /* Code For OrderItemCond Goes Here */

      p.xexpression_.accept(new XExpressionVisitor<R,A>(), arg);
      p.xorderoption_.accept(new XOrderOptionVisitor<R,A>(), arg);
      p.xnullsoption_.accept(new XNullsOptionVisitor<R,A>(), arg);

      return null;
    }

  }
  public class XOrderOptionVisitor<R,A> implements XOrderOption.Visitor<R,A>
  {
    public R visit(stupaq.cloudatlas.parser.QueryLanguage.Absyn.OrderOptionAsc p, A arg)
    {
      /* Code For OrderOptionAsc Goes Here */


      return null;
    }
    public R visit(stupaq.cloudatlas.parser.QueryLanguage.Absyn.OrderOptionDesc p, A arg)
    {
      /* Code For OrderOptionDesc Goes Here */


      return null;
    }
    public R visit(stupaq.cloudatlas.parser.QueryLanguage.Absyn.OrderOptionEmpty p, A arg)
    {
      /* Code For OrderOptionEmpty Goes Here */


      return null;
    }

  }
  public class XNullsOptionVisitor<R,A> implements XNullsOption.Visitor<R,A>
  {
    public R visit(stupaq.cloudatlas.parser.QueryLanguage.Absyn.NullsOptionFirst p, A arg)
    {
      /* Code For NullsOptionFirst Goes Here */


      return null;
    }
    public R visit(stupaq.cloudatlas.parser.QueryLanguage.Absyn.NullsOptionLast p, A arg)
    {
      /* Code For NullsOptionLast Goes Here */


      return null;
    }
    public R visit(stupaq.cloudatlas.parser.QueryLanguage.Absyn.NullsOptionEmpty p, A arg)
    {
      /* Code For NullsOptionEmpty Goes Here */


      return null;
    }

  }
  public class XSelectItemVisitor<R,A> implements XSelectItem.Visitor<R,A>
  {
    public R visit(stupaq.cloudatlas.parser.QueryLanguage.Absyn.SelectItem p, A arg)
    {
      /* Code For SelectItem Goes Here */

      p.xexpression_.accept(new XExpressionVisitor<R,A>(), arg);

      return null;
    }
    public R visit(stupaq.cloudatlas.parser.QueryLanguage.Absyn.SelectItemAs p, A arg)
    {
      /* Code For SelectItemAs Goes Here */

      p.xexpression_.accept(new XExpressionVisitor<R,A>(), arg);
      //p.xident_;

      return null;
    }

  }
  public class XExpressionVisitor<R,A> implements XExpression.Visitor<R,A>
  {
    public R visit(stupaq.cloudatlas.parser.QueryLanguage.Absyn.CondExprOr p, A arg)
    {
      /* Code For CondExprOr Goes Here */

      p.xexpression_1.accept(new XExpressionVisitor<R,A>(), arg);
      p.xexpression_2.accept(new XExpressionVisitor<R,A>(), arg);

      return null;
    }
    public R visit(stupaq.cloudatlas.parser.QueryLanguage.Absyn.CondExprAnd p, A arg)
    {
      /* Code For CondExprAnd Goes Here */

      p.xexpression_1.accept(new XExpressionVisitor<R,A>(), arg);
      p.xexpression_2.accept(new XExpressionVisitor<R,A>(), arg);

      return null;
    }
    public R visit(stupaq.cloudatlas.parser.QueryLanguage.Absyn.CondExprNot p, A arg)
    {
      /* Code For CondExprNot Goes Here */

      p.xexpression_.accept(new XExpressionVisitor<R,A>(), arg);

      return null;
    }
    public R visit(stupaq.cloudatlas.parser.QueryLanguage.Absyn.BoolExprRegex p, A arg)
    {
      /* Code For BoolExprRegex Goes Here */

      p.xexpression_.accept(new XExpressionVisitor<R,A>(), arg);
      //p.string_;

      return null;
    }
    public R visit(stupaq.cloudatlas.parser.QueryLanguage.Absyn.BoolExprRel p, A arg)
    {
      /* Code For BoolExprRel Goes Here */

      p.xexpression_1.accept(new XExpressionVisitor<R,A>(), arg);
      p.xrelop_.accept(new XRelOpVisitor<R,A>(), arg);
      p.xexpression_2.accept(new XExpressionVisitor<R,A>(), arg);

      return null;
    }
    public R visit(stupaq.cloudatlas.parser.QueryLanguage.Absyn.ArithExprAdd p, A arg)
    {
      /* Code For ArithExprAdd Goes Here */

      p.xexpression_1.accept(new XExpressionVisitor<R,A>(), arg);
      p.xarithopadd_.accept(new XArithOpAddVisitor<R,A>(), arg);
      p.xexpression_2.accept(new XExpressionVisitor<R,A>(), arg);

      return null;
    }
    public R visit(stupaq.cloudatlas.parser.QueryLanguage.Absyn.ArithExprMultiply p, A arg)
    {
      /* Code For ArithExprMultiply Goes Here */

      p.xexpression_1.accept(new XExpressionVisitor<R,A>(), arg);
      p.xarithopmultiply_.accept(new XArithOpMultiplyVisitor<R,A>(), arg);
      p.xexpression_2.accept(new XExpressionVisitor<R,A>(), arg);

      return null;
    }
    public R visit(stupaq.cloudatlas.parser.QueryLanguage.Absyn.ArithExprNeg p, A arg)
    {
      /* Code For ArithExprNeg Goes Here */

      p.xexpression_.accept(new XExpressionVisitor<R,A>(), arg);

      return null;
    }
    public R visit(stupaq.cloudatlas.parser.QueryLanguage.Absyn.BasicExprVar p, A arg)
    {
      /* Code For BasicExprVar Goes Here */

      //p.xident_;

      return null;
    }
    public R visit(stupaq.cloudatlas.parser.QueryLanguage.Absyn.BasicExprCall p, A arg)
    {
      /* Code For BasicExprCall Goes Here */

      //p.xident_;
      for (XExpression x : p.listxexpression_) {
      }

      return null;
    }
    public R visit(stupaq.cloudatlas.parser.QueryLanguage.Absyn.BasicExprString p, A arg)
    {
      /* Code For BasicExprString Goes Here */

      //p.string_;

      return null;
    }
    public R visit(stupaq.cloudatlas.parser.QueryLanguage.Absyn.BasicExprTrue p, A arg)
    {
      /* Code For BasicExprTrue Goes Here */

      p.xboolconst_.accept(new XBoolConstVisitor<R,A>(), arg);

      return null;
    }
    public R visit(stupaq.cloudatlas.parser.QueryLanguage.Absyn.BasicExprInt p, A arg)
    {
      /* Code For BasicExprInt Goes Here */

      //p.integer_;

      return null;
    }
    public R visit(stupaq.cloudatlas.parser.QueryLanguage.Absyn.BasicExprDouble p, A arg)
    {
      /* Code For BasicExprDouble Goes Here */

      //p.double_;

      return null;
    }
    public R visit(stupaq.cloudatlas.parser.QueryLanguage.Absyn.BasicExprStmt p, A arg)
    {
      /* Code For BasicExprStmt Goes Here */

      p.xstatement_.accept(new XStatementVisitor<R,A>(), arg);

      return null;
    }

  }
  public class XArithOpAddVisitor<R,A> implements XArithOpAdd.Visitor<R,A>
  {
    public R visit(stupaq.cloudatlas.parser.QueryLanguage.Absyn.ArithOpAdd p, A arg)
    {
      /* Code For ArithOpAdd Goes Here */


      return null;
    }
    public R visit(stupaq.cloudatlas.parser.QueryLanguage.Absyn.ArithOpSubstract p, A arg)
    {
      /* Code For ArithOpSubstract Goes Here */


      return null;
    }

  }
  public class XArithOpMultiplyVisitor<R,A> implements XArithOpMultiply.Visitor<R,A>
  {
    public R visit(stupaq.cloudatlas.parser.QueryLanguage.Absyn.ArithOpMultiply p, A arg)
    {
      /* Code For ArithOpMultiply Goes Here */


      return null;
    }
    public R visit(stupaq.cloudatlas.parser.QueryLanguage.Absyn.ArithOpDivide p, A arg)
    {
      /* Code For ArithOpDivide Goes Here */


      return null;
    }
    public R visit(stupaq.cloudatlas.parser.QueryLanguage.Absyn.ArithOpModulo p, A arg)
    {
      /* Code For ArithOpModulo Goes Here */


      return null;
    }

  }
  public class XRelOpVisitor<R,A> implements XRelOp.Visitor<R,A>
  {
    public R visit(stupaq.cloudatlas.parser.QueryLanguage.Absyn.RelOpEqual p, A arg)
    {
      /* Code For RelOpEqual Goes Here */


      return null;
    }
    public R visit(stupaq.cloudatlas.parser.QueryLanguage.Absyn.RelOpNotEqual p, A arg)
    {
      /* Code For RelOpNotEqual Goes Here */


      return null;
    }
    public R visit(stupaq.cloudatlas.parser.QueryLanguage.Absyn.RelOpGreater p, A arg)
    {
      /* Code For RelOpGreater Goes Here */


      return null;
    }
    public R visit(stupaq.cloudatlas.parser.QueryLanguage.Absyn.RelOpGreaterEqual p, A arg)
    {
      /* Code For RelOpGreaterEqual Goes Here */


      return null;
    }
    public R visit(stupaq.cloudatlas.parser.QueryLanguage.Absyn.RelOpLesser p, A arg)
    {
      /* Code For RelOpLesser Goes Here */


      return null;
    }
    public R visit(stupaq.cloudatlas.parser.QueryLanguage.Absyn.RelOpLesserEqual p, A arg)
    {
      /* Code For RelOpLesserEqual Goes Here */


      return null;
    }

  }
  public class XBoolConstVisitor<R,A> implements XBoolConst.Visitor<R,A>
  {
    public R visit(stupaq.cloudatlas.parser.QueryLanguage.Absyn.BoolConstTrue p, A arg)
    {
      /* Code For BoolConstTrue Goes Here */


      return null;
    }
    public R visit(stupaq.cloudatlas.parser.QueryLanguage.Absyn.BoolConstFalse p, A arg)
    {
      /* Code For BoolConstFalse Goes Here */


      return null;
    }

  }
}