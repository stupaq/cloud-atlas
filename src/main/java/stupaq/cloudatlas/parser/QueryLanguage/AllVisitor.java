package stupaq.cloudatlas.parser.QueryLanguage;

import stupaq.cloudatlas.parser.QueryLanguage.Absyn.*;

/** BNFC-Generated All Visitor */
public interface AllVisitor<R,A> extends
  stupaq.cloudatlas.parser.QueryLanguage.Absyn.XProgram.Visitor<R,A>,
  stupaq.cloudatlas.parser.QueryLanguage.Absyn.XStatement.Visitor<R,A>,
  stupaq.cloudatlas.parser.QueryLanguage.Absyn.XWhereClause.Visitor<R,A>,
  stupaq.cloudatlas.parser.QueryLanguage.Absyn.XOrderClause.Visitor<R,A>,
  stupaq.cloudatlas.parser.QueryLanguage.Absyn.XOrderItem.Visitor<R,A>,
  stupaq.cloudatlas.parser.QueryLanguage.Absyn.XOrderOption.Visitor<R,A>,
  stupaq.cloudatlas.parser.QueryLanguage.Absyn.XNullsOption.Visitor<R,A>,
  stupaq.cloudatlas.parser.QueryLanguage.Absyn.XSelectItem.Visitor<R,A>,
  stupaq.cloudatlas.parser.QueryLanguage.Absyn.XExpression.Visitor<R,A>,
  stupaq.cloudatlas.parser.QueryLanguage.Absyn.XArithOpAdd.Visitor<R,A>,
  stupaq.cloudatlas.parser.QueryLanguage.Absyn.XArithOpMultiply.Visitor<R,A>,
  stupaq.cloudatlas.parser.QueryLanguage.Absyn.XRelOp.Visitor<R,A>,
  stupaq.cloudatlas.parser.QueryLanguage.Absyn.XBoolConst.Visitor<R,A>
{}
