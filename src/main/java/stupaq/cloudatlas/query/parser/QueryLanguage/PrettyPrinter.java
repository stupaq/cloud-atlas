package stupaq.cloudatlas.query.parser.QueryLanguage;

import stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.XExpression;
import stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.XOrderItem;
import stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.XSelectItem;
import stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.XStatement;

public class PrettyPrinter
{
  //For certain applications increasing the initial size of the buffer may improve performance.
  private static final int INITIAL_BUFFER_SIZE = 128;
  //You may wish to change the parentheses used in precedence.
  private static final String _L_PAREN = new String("(");
  private static final String _R_PAREN = new String(")");
  //You may wish to change render
  private static void render(String s)
  {
    if (s.equals("{"))
    {
       buf_.append("\n");
       indent();
       buf_.append(s);
       _n_ = _n_ + 2;
       buf_.append("\n");
       indent();
    }
    else if (s.equals("(") || s.equals("["))
       buf_.append(s);
    else if (s.equals(")") || s.equals("]"))
    {
       backup();
       buf_.append(s);
       buf_.append(" ");
    }
    else if (s.equals("}"))
    {
       _n_ = _n_ - 2;
       backup();
       backup();
       buf_.append(s);
       buf_.append("\n");
       indent();
    }
    else if (s.equals(","))
    {
       backup();
       buf_.append(s);
       buf_.append(" ");
    }
    else if (s.equals(";"))
    {
       backup();
       buf_.append(s);
       buf_.append("\n");
       indent();
    }
    else if (s.equals("")) return;
    else
    {
       buf_.append(s);
       buf_.append(" ");
    }
  }


  //  print and show methods are defined for each category.
  public static String print(stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.XProgram foo)
  {
    pp(foo, 0);
    trim();
    String temp = buf_.toString();
    buf_.delete(0,buf_.length());
    return temp;
  }
  public static String show(stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.XProgram foo)
  {
    sh(foo);
    String temp = buf_.toString();
    buf_.delete(0,buf_.length());
    return temp;
  }
  public static String print(stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.ListXStatement foo)
  {
    pp(foo, 0);
    trim();
    String temp = buf_.toString();
    buf_.delete(0,buf_.length());
    return temp;
  }
  public static String show(stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.ListXStatement foo)
  {
    sh(foo);
    String temp = buf_.toString();
    buf_.delete(0,buf_.length());
    return temp;
  }
  public static String print(stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.XStatement foo)
  {
    pp(foo, 0);
    trim();
    String temp = buf_.toString();
    buf_.delete(0,buf_.length());
    return temp;
  }
  public static String show(stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.XStatement foo)
  {
    sh(foo);
    String temp = buf_.toString();
    buf_.delete(0,buf_.length());
    return temp;
  }
  public static String print(stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.ListXSelectItem foo)
  {
    pp(foo, 0);
    trim();
    String temp = buf_.toString();
    buf_.delete(0,buf_.length());
    return temp;
  }
  public static String show(stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.ListXSelectItem foo)
  {
    sh(foo);
    String temp = buf_.toString();
    buf_.delete(0,buf_.length());
    return temp;
  }
  public static String print(stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.XWhereClause foo)
  {
    pp(foo, 0);
    trim();
    String temp = buf_.toString();
    buf_.delete(0,buf_.length());
    return temp;
  }
  public static String show(stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.XWhereClause foo)
  {
    sh(foo);
    String temp = buf_.toString();
    buf_.delete(0,buf_.length());
    return temp;
  }
  public static String print(stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.XOrderClause foo)
  {
    pp(foo, 0);
    trim();
    String temp = buf_.toString();
    buf_.delete(0,buf_.length());
    return temp;
  }
  public static String show(stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.XOrderClause foo)
  {
    sh(foo);
    String temp = buf_.toString();
    buf_.delete(0,buf_.length());
    return temp;
  }
  public static String print(stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.XOrderItem foo)
  {
    pp(foo, 0);
    trim();
    String temp = buf_.toString();
    buf_.delete(0,buf_.length());
    return temp;
  }
  public static String show(stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.XOrderItem foo)
  {
    sh(foo);
    String temp = buf_.toString();
    buf_.delete(0,buf_.length());
    return temp;
  }
  public static String print(stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.ListXOrderItem foo)
  {
    pp(foo, 0);
    trim();
    String temp = buf_.toString();
    buf_.delete(0,buf_.length());
    return temp;
  }
  public static String show(stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.ListXOrderItem foo)
  {
    sh(foo);
    String temp = buf_.toString();
    buf_.delete(0,buf_.length());
    return temp;
  }
  public static String print(stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.XOrderOption foo)
  {
    pp(foo, 0);
    trim();
    String temp = buf_.toString();
    buf_.delete(0,buf_.length());
    return temp;
  }
  public static String show(stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.XOrderOption foo)
  {
    sh(foo);
    String temp = buf_.toString();
    buf_.delete(0,buf_.length());
    return temp;
  }
  public static String print(stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.XNullsOption foo)
  {
    pp(foo, 0);
    trim();
    String temp = buf_.toString();
    buf_.delete(0,buf_.length());
    return temp;
  }
  public static String show(stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.XNullsOption foo)
  {
    sh(foo);
    String temp = buf_.toString();
    buf_.delete(0,buf_.length());
    return temp;
  }
  public static String print(stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.XSelectItem foo)
  {
    pp(foo, 0);
    trim();
    String temp = buf_.toString();
    buf_.delete(0,buf_.length());
    return temp;
  }
  public static String show(stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.XSelectItem foo)
  {
    sh(foo);
    String temp = buf_.toString();
    buf_.delete(0,buf_.length());
    return temp;
  }
  public static String print(stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.XExpression foo)
  {
    pp(foo, 0);
    trim();
    String temp = buf_.toString();
    buf_.delete(0,buf_.length());
    return temp;
  }
  public static String show(stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.XExpression foo)
  {
    sh(foo);
    String temp = buf_.toString();
    buf_.delete(0,buf_.length());
    return temp;
  }
  public static String print(stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.ListXExpression foo)
  {
    pp(foo, 0);
    trim();
    String temp = buf_.toString();
    buf_.delete(0,buf_.length());
    return temp;
  }
  public static String show(stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.ListXExpression foo)
  {
    sh(foo);
    String temp = buf_.toString();
    buf_.delete(0,buf_.length());
    return temp;
  }
  public static String print(stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.XArithOpAdd foo)
  {
    pp(foo, 0);
    trim();
    String temp = buf_.toString();
    buf_.delete(0,buf_.length());
    return temp;
  }
  public static String show(stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.XArithOpAdd foo)
  {
    sh(foo);
    String temp = buf_.toString();
    buf_.delete(0,buf_.length());
    return temp;
  }
  public static String print(stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.XArithOpMultiply foo)
  {
    pp(foo, 0);
    trim();
    String temp = buf_.toString();
    buf_.delete(0,buf_.length());
    return temp;
  }
  public static String show(stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.XArithOpMultiply foo)
  {
    sh(foo);
    String temp = buf_.toString();
    buf_.delete(0,buf_.length());
    return temp;
  }
  public static String print(stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.XRelOp foo)
  {
    pp(foo, 0);
    trim();
    String temp = buf_.toString();
    buf_.delete(0,buf_.length());
    return temp;
  }
  public static String show(stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.XRelOp foo)
  {
    sh(foo);
    String temp = buf_.toString();
    buf_.delete(0,buf_.length());
    return temp;
  }
  public static String print(stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.XBoolConst foo)
  {
    pp(foo, 0);
    trim();
    String temp = buf_.toString();
    buf_.delete(0,buf_.length());
    return temp;
  }
  public static String show(stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.XBoolConst foo)
  {
    sh(foo);
    String temp = buf_.toString();
    buf_.delete(0,buf_.length());
    return temp;
  }
  /***   You shouldn't need to change anything beyond this point.   ***/

  private static void pp(stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.XProgram foo, int _i_)
  {
    if (foo instanceof stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.Program)
    {
       stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.Program _program = (stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.Program) foo;
       if (_i_ > 0) render(_L_PAREN);
       pp(_program.listxstatement_, 0);
       if (_i_ > 0) render(_R_PAREN);
    }
  }

  private static void pp(stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.ListXStatement foo, int _i_)
  {
     for (java.util.Iterator<XStatement> it = foo.iterator(); it.hasNext();)
     {
       pp(it.next(), 0);
       if (it.hasNext()) {
         render(";");
       } else {
         render("");
       }
     }
  }

  private static void pp(stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.XStatement foo, int _i_)
  {
    if (foo instanceof stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.Statement)
    {
       stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.Statement _statement = (stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.Statement) foo;
       if (_i_ > 0) render(_L_PAREN);
       render("SELECT");
       pp(_statement.listxselectitem_, 0);
       pp(_statement.xwhereclause_, 0);
       pp(_statement.xorderclause_, 0);
       if (_i_ > 0) render(_R_PAREN);
    }
  }

  private static void pp(stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.ListXSelectItem foo, int _i_)
  {
     for (java.util.Iterator<XSelectItem> it = foo.iterator(); it.hasNext();)
     {
       pp(it.next(), 0);
       if (it.hasNext()) {
         render(",");
       } else {
         render("");
       }
     }
  }

  private static void pp(stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.XWhereClause foo, int _i_)
  {
    if (foo instanceof stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.WhereClause)
    {
       stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.WhereClause _whereclause = (stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.WhereClause) foo;
       if (_i_ > 0) render(_L_PAREN);
       render("WHERE");
       pp(_whereclause.xexpression_, 0);
       if (_i_ > 0) render(_R_PAREN);
    }
    else     if (foo instanceof stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.WhereClauseEmpty)
    {
       stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.WhereClauseEmpty _whereclauseempty = (stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.WhereClauseEmpty) foo;
       if (_i_ > 0) render(_L_PAREN);
       if (_i_ > 0) render(_R_PAREN);
    }
  }

  private static void pp(stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.XOrderClause foo, int _i_)
  {
    if (foo instanceof stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.OrderClause)
    {
       stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.OrderClause _orderclause = (stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.OrderClause) foo;
       if (_i_ > 0) render(_L_PAREN);
       render("ORDER");
       render("BY");
       pp(_orderclause.listxorderitem_, 0);
       if (_i_ > 0) render(_R_PAREN);
    }
    else     if (foo instanceof stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.OrderClauseEmpty)
    {
       stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.OrderClauseEmpty _orderclauseempty = (stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.OrderClauseEmpty) foo;
       if (_i_ > 0) render(_L_PAREN);
       if (_i_ > 0) render(_R_PAREN);
    }
  }

  private static void pp(stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.XOrderItem foo, int _i_)
  {
    if (foo instanceof stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.OrderItemCond)
    {
       stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.OrderItemCond _orderitemcond = (stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.OrderItemCond) foo;
       if (_i_ > 0) render(_L_PAREN);
       pp(_orderitemcond.xexpression_, 0);
       pp(_orderitemcond.xorderoption_, 0);
       pp(_orderitemcond.xnullsoption_, 0);
       if (_i_ > 0) render(_R_PAREN);
    }
  }

  private static void pp(stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.ListXOrderItem foo, int _i_)
  {
     for (java.util.Iterator<XOrderItem> it = foo.iterator(); it.hasNext();)
     {
       pp(it.next(), 0);
       if (it.hasNext()) {
         render(",");
       } else {
         render("");
       }
     }
  }

  private static void pp(stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.XOrderOption foo, int _i_)
  {
    if (foo instanceof stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.OrderOptionAsc)
    {
       stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.OrderOptionAsc _orderoptionasc = (stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.OrderOptionAsc) foo;
       if (_i_ > 0) render(_L_PAREN);
       render("ASC");
       if (_i_ > 0) render(_R_PAREN);
    }
    else     if (foo instanceof stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.OrderOptionDesc)
    {
       stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.OrderOptionDesc _orderoptiondesc = (stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.OrderOptionDesc) foo;
       if (_i_ > 0) render(_L_PAREN);
       render("DESC");
       if (_i_ > 0) render(_R_PAREN);
    }
    else     if (foo instanceof stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.OrderOptionEmpty)
    {
       stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.OrderOptionEmpty _orderoptionempty = (stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.OrderOptionEmpty) foo;
       if (_i_ > 0) render(_L_PAREN);
       if (_i_ > 0) render(_R_PAREN);
    }
  }

  private static void pp(stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.XNullsOption foo, int _i_)
  {
    if (foo instanceof stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.NullsOptionFirst)
    {
       stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.NullsOptionFirst _nullsoptionfirst = (stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.NullsOptionFirst) foo;
       if (_i_ > 0) render(_L_PAREN);
       render("NULLS");
       render("FIRST");
       if (_i_ > 0) render(_R_PAREN);
    }
    else     if (foo instanceof stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.NullsOptionLast)
    {
       stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.NullsOptionLast _nullsoptionlast = (stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.NullsOptionLast) foo;
       if (_i_ > 0) render(_L_PAREN);
       render("NULLS");
       render("LAST");
       if (_i_ > 0) render(_R_PAREN);
    }
    else     if (foo instanceof stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.NullsOptionEmpty)
    {
       stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.NullsOptionEmpty _nullsoptionempty = (stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.NullsOptionEmpty) foo;
       if (_i_ > 0) render(_L_PAREN);
       if (_i_ > 0) render(_R_PAREN);
    }
  }

  private static void pp(stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.XSelectItem foo, int _i_)
  {
    if (foo instanceof stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.SelectItem)
    {
       stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.SelectItem _selectitem = (stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.SelectItem) foo;
       if (_i_ > 0) render(_L_PAREN);
       pp(_selectitem.xexpression_, 0);
       if (_i_ > 0) render(_R_PAREN);
    }
    else     if (foo instanceof stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.SelectItemAs)
    {
       stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.SelectItemAs _selectitemas = (stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.SelectItemAs) foo;
       if (_i_ > 0) render(_L_PAREN);
       pp(_selectitemas.xexpression_, 0);
       render("AS");
       pp(_selectitemas.xident_, 0);
       if (_i_ > 0) render(_R_PAREN);
    }
  }

  private static void pp(stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.XExpression foo, int _i_)
  {
    if (foo instanceof stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.CondExprOr)
    {
       stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.CondExprOr _condexpror = (stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.CondExprOr) foo;
       if (_i_ > 0) render(_L_PAREN);
       pp(_condexpror.xexpression_1, 0);
       render("OR");
       pp(_condexpror.xexpression_2, 1);
       if (_i_ > 0) render(_R_PAREN);
    }
    else     if (foo instanceof stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.CondExprAnd)
    {
       stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.CondExprAnd _condexprand = (stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.CondExprAnd) foo;
       if (_i_ > 1) render(_L_PAREN);
       pp(_condexprand.xexpression_1, 1);
       render("AND");
       pp(_condexprand.xexpression_2, 2);
       if (_i_ > 1) render(_R_PAREN);
    }
    else     if (foo instanceof stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.CondExprNot)
    {
       stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.CondExprNot _condexprnot = (stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.CondExprNot) foo;
       if (_i_ > 2) render(_L_PAREN);
       render("NOT");
       pp(_condexprnot.xexpression_, 3);
       if (_i_ > 2) render(_R_PAREN);
    }
    else     if (foo instanceof stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.BoolExprRegex)
    {
       stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.BoolExprRegex _boolexprregex = (stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.BoolExprRegex) foo;
       if (_i_ > 3) render(_L_PAREN);
       pp(_boolexprregex.xexpression_, 4);
       render("REGEXP");
       printQuoted(_boolexprregex.string_);
       if (_i_ > 3) render(_R_PAREN);
    }
    else     if (foo instanceof stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.BoolExprRel)
    {
       stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.BoolExprRel _boolexprrel = (stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.BoolExprRel) foo;
       if (_i_ > 4) render(_L_PAREN);
       pp(_boolexprrel.xexpression_1, 4);
       pp(_boolexprrel.xrelop_, 0);
       pp(_boolexprrel.xexpression_2, 5);
       if (_i_ > 4) render(_R_PAREN);
    }
    else     if (foo instanceof stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.ArithExprAdd)
    {
       stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.ArithExprAdd _arithexpradd = (stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.ArithExprAdd) foo;
       if (_i_ > 5) render(_L_PAREN);
       pp(_arithexpradd.xexpression_1, 5);
       pp(_arithexpradd.xarithopadd_, 0);
       pp(_arithexpradd.xexpression_2, 6);
       if (_i_ > 5) render(_R_PAREN);
    }
    else     if (foo instanceof stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.ArithExprMultiply)
    {
       stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.ArithExprMultiply _arithexprmultiply = (stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.ArithExprMultiply) foo;
       if (_i_ > 6) render(_L_PAREN);
       pp(_arithexprmultiply.xexpression_1, 6);
       pp(_arithexprmultiply.xarithopmultiply_, 0);
       pp(_arithexprmultiply.xexpression_2, 7);
       if (_i_ > 6) render(_R_PAREN);
    }
    else     if (foo instanceof stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.ArithExprNeg)
    {
       stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.ArithExprNeg _arithexprneg = (stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.ArithExprNeg) foo;
       if (_i_ > 7) render(_L_PAREN);
       render("-");
       pp(_arithexprneg.xexpression_, 8);
       if (_i_ > 7) render(_R_PAREN);
    }
    else     if (foo instanceof stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.BasicExprVar)
    {
       stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.BasicExprVar _basicexprvar = (stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.BasicExprVar) foo;
       if (_i_ > 8) render(_L_PAREN);
       pp(_basicexprvar.xident_, 0);
       if (_i_ > 8) render(_R_PAREN);
    }
    else     if (foo instanceof stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.BasicExprCall)
    {
       stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.BasicExprCall _basicexprcall = (stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.BasicExprCall) foo;
       if (_i_ > 8) render(_L_PAREN);
       pp(_basicexprcall.xident_, 0);
       render("(");
       pp(_basicexprcall.listxexpression_, 0);
       render(")");
       if (_i_ > 8) render(_R_PAREN);
    }
    else     if (foo instanceof stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.BasicExprString)
    {
       stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.BasicExprString _basicexprstring = (stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.BasicExprString) foo;
       if (_i_ > 8) render(_L_PAREN);
       printQuoted(_basicexprstring.string_);
       if (_i_ > 8) render(_R_PAREN);
    }
    else     if (foo instanceof stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.BasicExprTrue)
    {
       stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.BasicExprTrue _basicexprtrue = (stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.BasicExprTrue) foo;
       if (_i_ > 8) render(_L_PAREN);
       pp(_basicexprtrue.xboolconst_, 0);
       if (_i_ > 8) render(_R_PAREN);
    }
    else     if (foo instanceof stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.BasicExprInt)
    {
       stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.BasicExprInt _basicexprint = (stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.BasicExprInt) foo;
       if (_i_ > 8) render(_L_PAREN);
       pp(_basicexprint.integer_, 0);
       if (_i_ > 8) render(_R_PAREN);
    }
    else     if (foo instanceof stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.BasicExprDouble)
    {
       stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.BasicExprDouble _basicexprdouble = (stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.BasicExprDouble) foo;
       if (_i_ > 8) render(_L_PAREN);
       pp(_basicexprdouble.double_, 0);
       if (_i_ > 8) render(_R_PAREN);
    }
    else     if (foo instanceof stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.BasicExprStmt)
    {
       stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.BasicExprStmt _basicexprstmt = (stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.BasicExprStmt) foo;
       if (_i_ > 8) render(_L_PAREN);
       render("(");
       pp(_basicexprstmt.xstatement_, 0);
       render(")");
       if (_i_ > 8) render(_R_PAREN);
    }
  }

  private static void pp(stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.ListXExpression foo, int _i_)
  {
     for (java.util.Iterator<XExpression> it = foo.iterator(); it.hasNext();)
     {
       pp(it.next(), 0);
       if (it.hasNext()) {
         render(",");
       } else {
         render("");
       }
     }
  }

  private static void pp(stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.XArithOpAdd foo, int _i_)
  {
    if (foo instanceof stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.ArithOpAdd)
    {
       stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.ArithOpAdd _arithopadd = (stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.ArithOpAdd) foo;
       if (_i_ > 0) render(_L_PAREN);
       render("+");
       if (_i_ > 0) render(_R_PAREN);
    }
    else     if (foo instanceof stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.ArithOpSubstract)
    {
       stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.ArithOpSubstract _arithopsubstract = (stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.ArithOpSubstract) foo;
       if (_i_ > 0) render(_L_PAREN);
       render("-");
       if (_i_ > 0) render(_R_PAREN);
    }
  }

  private static void pp(stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.XArithOpMultiply foo, int _i_)
  {
    if (foo instanceof stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.ArithOpMultiply)
    {
       stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.ArithOpMultiply _arithopmultiply = (stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.ArithOpMultiply) foo;
       if (_i_ > 0) render(_L_PAREN);
       render("*");
       if (_i_ > 0) render(_R_PAREN);
    }
    else     if (foo instanceof stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.ArithOpDivide)
    {
       stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.ArithOpDivide _arithopdivide = (stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.ArithOpDivide) foo;
       if (_i_ > 0) render(_L_PAREN);
       render("/");
       if (_i_ > 0) render(_R_PAREN);
    }
    else     if (foo instanceof stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.ArithOpModulo)
    {
       stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.ArithOpModulo _arithopmodulo = (stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.ArithOpModulo) foo;
       if (_i_ > 0) render(_L_PAREN);
       render("%");
       if (_i_ > 0) render(_R_PAREN);
    }
  }

  private static void pp(stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.XRelOp foo, int _i_)
  {
    if (foo instanceof stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.RelOpEqual)
    {
       stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.RelOpEqual _relopequal = (stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.RelOpEqual) foo;
       if (_i_ > 0) render(_L_PAREN);
       render("=");
       if (_i_ > 0) render(_R_PAREN);
    }
    else     if (foo instanceof stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.RelOpNotEqual)
    {
       stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.RelOpNotEqual _relopnotequal = (stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.RelOpNotEqual) foo;
       if (_i_ > 0) render(_L_PAREN);
       render("<>");
       if (_i_ > 0) render(_R_PAREN);
    }
    else     if (foo instanceof stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.RelOpGreater)
    {
       stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.RelOpGreater _relopgreater = (stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.RelOpGreater) foo;
       if (_i_ > 0) render(_L_PAREN);
       render(">");
       if (_i_ > 0) render(_R_PAREN);
    }
    else     if (foo instanceof stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.RelOpGreaterEqual)
    {
       stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.RelOpGreaterEqual _relopgreaterequal = (stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.RelOpGreaterEqual) foo;
       if (_i_ > 0) render(_L_PAREN);
       render(">=");
       if (_i_ > 0) render(_R_PAREN);
    }
    else     if (foo instanceof stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.RelOpLesser)
    {
       stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.RelOpLesser _reloplesser = (stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.RelOpLesser) foo;
       if (_i_ > 0) render(_L_PAREN);
       render("<");
       if (_i_ > 0) render(_R_PAREN);
    }
    else     if (foo instanceof stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.RelOpLesserEqual)
    {
       stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.RelOpLesserEqual _reloplesserequal = (stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.RelOpLesserEqual) foo;
       if (_i_ > 0) render(_L_PAREN);
       render("<=");
       if (_i_ > 0) render(_R_PAREN);
    }
  }

  private static void pp(stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.XBoolConst foo, int _i_)
  {
    if (foo instanceof stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.BoolConstTrue)
    {
       stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.BoolConstTrue _boolconsttrue = (stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.BoolConstTrue) foo;
       if (_i_ > 0) render(_L_PAREN);
       render("true");
       if (_i_ > 0) render(_R_PAREN);
    }
    else     if (foo instanceof stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.BoolConstFalse)
    {
       stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.BoolConstFalse _boolconstfalse = (stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.BoolConstFalse) foo;
       if (_i_ > 0) render(_L_PAREN);
       render("false");
       if (_i_ > 0) render(_R_PAREN);
    }
  }


  private static void sh(stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.XProgram foo)
  {
    if (foo instanceof stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.Program)
    {
       stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.Program _program = (stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.Program) foo;
       render("(");
       render("Program");
       render("[");
       sh(_program.listxstatement_);
       render("]");
       render(")");
    }
  }

  private static void sh(stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.ListXStatement foo)
  {
     for (java.util.Iterator<XStatement> it = foo.iterator(); it.hasNext();)
     {
       sh(it.next());
       if (it.hasNext())
         render(",");
     }
  }

  private static void sh(stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.XStatement foo)
  {
    if (foo instanceof stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.Statement)
    {
       stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.Statement _statement = (stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.Statement) foo;
       render("(");
       render("Statement");
       render("[");
       sh(_statement.listxselectitem_);
       render("]");
       sh(_statement.xwhereclause_);
       sh(_statement.xorderclause_);
       render(")");
    }
  }

  private static void sh(stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.ListXSelectItem foo)
  {
     for (java.util.Iterator<XSelectItem> it = foo.iterator(); it.hasNext();)
     {
       sh(it.next());
       if (it.hasNext())
         render(",");
     }
  }

  private static void sh(stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.XWhereClause foo)
  {
    if (foo instanceof stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.WhereClause)
    {
       stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.WhereClause _whereclause = (stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.WhereClause) foo;
       render("(");
       render("WhereClause");
       sh(_whereclause.xexpression_);
       render(")");
    }
    if (foo instanceof stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.WhereClauseEmpty)
    {
       stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.WhereClauseEmpty _whereclauseempty = (stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.WhereClauseEmpty) foo;
       render("WhereClauseEmpty");
    }
  }

  private static void sh(stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.XOrderClause foo)
  {
    if (foo instanceof stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.OrderClause)
    {
       stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.OrderClause _orderclause = (stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.OrderClause) foo;
       render("(");
       render("OrderClause");
       render("[");
       sh(_orderclause.listxorderitem_);
       render("]");
       render(")");
    }
    if (foo instanceof stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.OrderClauseEmpty)
    {
       stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.OrderClauseEmpty _orderclauseempty = (stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.OrderClauseEmpty) foo;
       render("OrderClauseEmpty");
    }
  }

  private static void sh(stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.XOrderItem foo)
  {
    if (foo instanceof stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.OrderItemCond)
    {
       stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.OrderItemCond _orderitemcond = (stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.OrderItemCond) foo;
       render("(");
       render("OrderItemCond");
       sh(_orderitemcond.xexpression_);
       sh(_orderitemcond.xorderoption_);
       sh(_orderitemcond.xnullsoption_);
       render(")");
    }
  }

  private static void sh(stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.ListXOrderItem foo)
  {
     for (java.util.Iterator<XOrderItem> it = foo.iterator(); it.hasNext();)
     {
       sh(it.next());
       if (it.hasNext())
         render(",");
     }
  }

  private static void sh(stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.XOrderOption foo)
  {
    if (foo instanceof stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.OrderOptionAsc)
    {
       stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.OrderOptionAsc _orderoptionasc = (stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.OrderOptionAsc) foo;
       render("OrderOptionAsc");
    }
    if (foo instanceof stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.OrderOptionDesc)
    {
       stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.OrderOptionDesc _orderoptiondesc = (stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.OrderOptionDesc) foo;
       render("OrderOptionDesc");
    }
    if (foo instanceof stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.OrderOptionEmpty)
    {
       stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.OrderOptionEmpty _orderoptionempty = (stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.OrderOptionEmpty) foo;
       render("OrderOptionEmpty");
    }
  }

  private static void sh(stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.XNullsOption foo)
  {
    if (foo instanceof stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.NullsOptionFirst)
    {
       stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.NullsOptionFirst _nullsoptionfirst = (stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.NullsOptionFirst) foo;
       render("NullsOptionFirst");
    }
    if (foo instanceof stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.NullsOptionLast)
    {
       stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.NullsOptionLast _nullsoptionlast = (stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.NullsOptionLast) foo;
       render("NullsOptionLast");
    }
    if (foo instanceof stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.NullsOptionEmpty)
    {
       stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.NullsOptionEmpty _nullsoptionempty = (stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.NullsOptionEmpty) foo;
       render("NullsOptionEmpty");
    }
  }

  private static void sh(stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.XSelectItem foo)
  {
    if (foo instanceof stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.SelectItem)
    {
       stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.SelectItem _selectitem = (stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.SelectItem) foo;
       render("(");
       render("SelectItem");
       sh(_selectitem.xexpression_);
       render(")");
    }
    if (foo instanceof stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.SelectItemAs)
    {
       stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.SelectItemAs _selectitemas = (stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.SelectItemAs) foo;
       render("(");
       render("SelectItemAs");
       sh(_selectitemas.xexpression_);
       sh(_selectitemas.xident_);
       render(")");
    }
  }

  private static void sh(stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.XExpression foo)
  {
    if (foo instanceof stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.CondExprOr)
    {
       stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.CondExprOr _condexpror = (stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.CondExprOr) foo;
       render("(");
       render("CondExprOr");
       sh(_condexpror.xexpression_1);
       sh(_condexpror.xexpression_2);
       render(")");
    }
    if (foo instanceof stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.CondExprAnd)
    {
       stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.CondExprAnd _condexprand = (stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.CondExprAnd) foo;
       render("(");
       render("CondExprAnd");
       sh(_condexprand.xexpression_1);
       sh(_condexprand.xexpression_2);
       render(")");
    }
    if (foo instanceof stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.CondExprNot)
    {
       stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.CondExprNot _condexprnot = (stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.CondExprNot) foo;
       render("(");
       render("CondExprNot");
       sh(_condexprnot.xexpression_);
       render(")");
    }
    if (foo instanceof stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.BoolExprRegex)
    {
       stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.BoolExprRegex _boolexprregex = (stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.BoolExprRegex) foo;
       render("(");
       render("BoolExprRegex");
       sh(_boolexprregex.xexpression_);
       sh(_boolexprregex.string_);
       render(")");
    }
    if (foo instanceof stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.BoolExprRel)
    {
       stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.BoolExprRel _boolexprrel = (stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.BoolExprRel) foo;
       render("(");
       render("BoolExprRel");
       sh(_boolexprrel.xexpression_1);
       sh(_boolexprrel.xrelop_);
       sh(_boolexprrel.xexpression_2);
       render(")");
    }
    if (foo instanceof stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.ArithExprAdd)
    {
       stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.ArithExprAdd _arithexpradd = (stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.ArithExprAdd) foo;
       render("(");
       render("ArithExprAdd");
       sh(_arithexpradd.xexpression_1);
       sh(_arithexpradd.xarithopadd_);
       sh(_arithexpradd.xexpression_2);
       render(")");
    }
    if (foo instanceof stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.ArithExprMultiply)
    {
       stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.ArithExprMultiply _arithexprmultiply = (stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.ArithExprMultiply) foo;
       render("(");
       render("ArithExprMultiply");
       sh(_arithexprmultiply.xexpression_1);
       sh(_arithexprmultiply.xarithopmultiply_);
       sh(_arithexprmultiply.xexpression_2);
       render(")");
    }
    if (foo instanceof stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.ArithExprNeg)
    {
       stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.ArithExprNeg _arithexprneg = (stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.ArithExprNeg) foo;
       render("(");
       render("ArithExprNeg");
       sh(_arithexprneg.xexpression_);
       render(")");
    }
    if (foo instanceof stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.BasicExprVar)
    {
       stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.BasicExprVar _basicexprvar = (stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.BasicExprVar) foo;
       render("(");
       render("BasicExprVar");
       sh(_basicexprvar.xident_);
       render(")");
    }
    if (foo instanceof stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.BasicExprCall)
    {
       stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.BasicExprCall _basicexprcall = (stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.BasicExprCall) foo;
       render("(");
       render("BasicExprCall");
       sh(_basicexprcall.xident_);
       render("[");
       sh(_basicexprcall.listxexpression_);
       render("]");
       render(")");
    }
    if (foo instanceof stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.BasicExprString)
    {
       stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.BasicExprString _basicexprstring = (stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.BasicExprString) foo;
       render("(");
       render("BasicExprString");
       sh(_basicexprstring.string_);
       render(")");
    }
    if (foo instanceof stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.BasicExprTrue)
    {
       stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.BasicExprTrue _basicexprtrue = (stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.BasicExprTrue) foo;
       render("(");
       render("BasicExprTrue");
       sh(_basicexprtrue.xboolconst_);
       render(")");
    }
    if (foo instanceof stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.BasicExprInt)
    {
       stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.BasicExprInt _basicexprint = (stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.BasicExprInt) foo;
       render("(");
       render("BasicExprInt");
       sh(_basicexprint.integer_);
       render(")");
    }
    if (foo instanceof stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.BasicExprDouble)
    {
       stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.BasicExprDouble _basicexprdouble = (stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.BasicExprDouble) foo;
       render("(");
       render("BasicExprDouble");
       sh(_basicexprdouble.double_);
       render(")");
    }
    if (foo instanceof stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.BasicExprStmt)
    {
       stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.BasicExprStmt _basicexprstmt = (stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.BasicExprStmt) foo;
       render("(");
       render("BasicExprStmt");
       sh(_basicexprstmt.xstatement_);
       render(")");
    }
  }

  private static void sh(stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.ListXExpression foo)
  {
     for (java.util.Iterator<XExpression> it = foo.iterator(); it.hasNext();)
     {
       sh(it.next());
       if (it.hasNext())
         render(",");
     }
  }

  private static void sh(stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.XArithOpAdd foo)
  {
    if (foo instanceof stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.ArithOpAdd)
    {
       stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.ArithOpAdd _arithopadd = (stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.ArithOpAdd) foo;
       render("ArithOpAdd");
    }
    if (foo instanceof stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.ArithOpSubstract)
    {
       stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.ArithOpSubstract _arithopsubstract = (stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.ArithOpSubstract) foo;
       render("ArithOpSubstract");
    }
  }

  private static void sh(stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.XArithOpMultiply foo)
  {
    if (foo instanceof stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.ArithOpMultiply)
    {
       stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.ArithOpMultiply _arithopmultiply = (stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.ArithOpMultiply) foo;
       render("ArithOpMultiply");
    }
    if (foo instanceof stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.ArithOpDivide)
    {
       stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.ArithOpDivide _arithopdivide = (stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.ArithOpDivide) foo;
       render("ArithOpDivide");
    }
    if (foo instanceof stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.ArithOpModulo)
    {
       stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.ArithOpModulo _arithopmodulo = (stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.ArithOpModulo) foo;
       render("ArithOpModulo");
    }
  }

  private static void sh(stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.XRelOp foo)
  {
    if (foo instanceof stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.RelOpEqual)
    {
       stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.RelOpEqual _relopequal = (stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.RelOpEqual) foo;
       render("RelOpEqual");
    }
    if (foo instanceof stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.RelOpNotEqual)
    {
       stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.RelOpNotEqual _relopnotequal = (stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.RelOpNotEqual) foo;
       render("RelOpNotEqual");
    }
    if (foo instanceof stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.RelOpGreater)
    {
       stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.RelOpGreater _relopgreater = (stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.RelOpGreater) foo;
       render("RelOpGreater");
    }
    if (foo instanceof stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.RelOpGreaterEqual)
    {
       stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.RelOpGreaterEqual _relopgreaterequal = (stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.RelOpGreaterEqual) foo;
       render("RelOpGreaterEqual");
    }
    if (foo instanceof stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.RelOpLesser)
    {
       stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.RelOpLesser _reloplesser = (stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.RelOpLesser) foo;
       render("RelOpLesser");
    }
    if (foo instanceof stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.RelOpLesserEqual)
    {
       stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.RelOpLesserEqual _reloplesserequal = (stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.RelOpLesserEqual) foo;
       render("RelOpLesserEqual");
    }
  }

  private static void sh(stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.XBoolConst foo)
  {
    if (foo instanceof stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.BoolConstTrue)
    {
       stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.BoolConstTrue _boolconsttrue = (stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.BoolConstTrue) foo;
       render("BoolConstTrue");
    }
    if (foo instanceof stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.BoolConstFalse)
    {
       stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.BoolConstFalse _boolconstfalse = (stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.BoolConstFalse) foo;
       render("BoolConstFalse");
    }
  }


  private static void pp(Integer n, int _i_) { buf_.append(n); buf_.append(" "); }
  private static void pp(Double d, int _i_) { buf_.append(d); buf_.append(" "); }
  private static void pp(String s, int _i_) { buf_.append(s); buf_.append(" "); }
  private static void pp(Character c, int _i_) { buf_.append("'" + c.toString() + "'"); buf_.append(" "); }
  private static void sh(Integer n) { render(n.toString()); }
  private static void sh(Double d) { render(d.toString()); }
  private static void sh(Character c) { render(c.toString()); }
  private static void sh(String s) { printQuoted(s); }
  private static void printQuoted(String s) { render("\"" + s + "\""); }
  private static void indent()
  {
    int n = _n_;
    while (n > 0)
    {
      buf_.append(" ");
      n--;
    }
  }
  private static void backup()
  {
     if (buf_.charAt(buf_.length() - 1) == ' ') {
      buf_.setLength(buf_.length() - 1);
    }
  }
  private static void trim()
  {
     while (buf_.length() > 0 && buf_.charAt(0) == ' ')
        buf_.deleteCharAt(0); 
    while (buf_.length() > 0 && buf_.charAt(buf_.length()-1) == ' ')
        buf_.deleteCharAt(buf_.length()-1);
  }
  private static int _n_ = 0;
  private static StringBuilder buf_ = new StringBuilder(INITIAL_BUFFER_SIZE);
}

