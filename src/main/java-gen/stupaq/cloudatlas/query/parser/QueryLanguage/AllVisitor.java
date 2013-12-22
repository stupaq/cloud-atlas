package stupaq.cloudatlas.query.parser.QueryLanguage;

/** BNFC-Generated All Visitor */
public interface AllVisitor<R,A> extends
  stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.XProgram.Visitor<R,A>,
  stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.XStatement.Visitor<R,A>,
  stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.XWhereClause.Visitor<R,A>,
  stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.XOrderClause.Visitor<R,A>,
  stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.XOrderItem.Visitor<R,A>,
  stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.XOrderOption.Visitor<R,A>,
  stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.XNullsOption.Visitor<R,A>,
  stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.XSelectItem.Visitor<R,A>,
  stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.XExpression.Visitor<R,A>,
  stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.XArithOpAdd.Visitor<R,A>,
  stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.XArithOpMultiply.Visitor<R,A>,
  stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.XRelOp.Visitor<R,A>,
  stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.XBoolConst.Visitor<R,A>
{}
