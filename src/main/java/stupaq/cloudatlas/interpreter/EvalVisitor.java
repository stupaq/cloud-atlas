package stupaq.cloudatlas.interpreter;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.collect.Ordering;

import java.util.Collections;
import java.util.Iterator;

import stupaq.cloudatlas.attribute.AttributeValue;
import stupaq.cloudatlas.attribute.types.CABoolean;
import stupaq.cloudatlas.interpreter.context.Context;
import stupaq.cloudatlas.interpreter.context.InputContext;
import stupaq.cloudatlas.interpreter.context.OutputContext;
import stupaq.cloudatlas.interpreter.context.OutputContext.CollectorOutputContext;
import stupaq.cloudatlas.interpreter.context.OutputContext.InnerSelectOutputContext;
import stupaq.cloudatlas.interpreter.data.AttributesRow;
import stupaq.cloudatlas.interpreter.data.AttributesTable;
import stupaq.cloudatlas.interpreter.errors.EvaluationException;
import stupaq.cloudatlas.interpreter.semantics.SemanticValue;
import stupaq.cloudatlas.interpreter.semantics.SemanticValue.SemanticValueCastException;
import stupaq.cloudatlas.interpreter.types.RList;
import stupaq.cloudatlas.interpreter.types.RSingle;
import stupaq.cloudatlas.parser.QueryLanguage.Absyn.*;

public class EvalVisitor {
  private final AttributesTable originalTable;

  public EvalVisitor(AttributesTable originalTable) {
    this.originalTable = originalTable;
  }

  public OutputContext eval(XProgram program) {
    OutputContext outputContext = new CollectorOutputContext();
    program.accept(new XProgramVisitor(), outputContext);
    return outputContext;
  }

  private class XProgramVisitor implements XProgram.Visitor<Void, OutputContext> {
    @Override
    public Void visit(Program p, OutputContext outputContext) {
      for (XStatement x : p.listxstatement_) {
        x.accept(new XStatementVisitor(), outputContext);
      }
      return null;
    }
  }

  private class XStatementVisitor implements XStatement.Visitor<SemanticValue, OutputContext> {
    @SuppressWarnings("unchecked")
    @Override
    public SemanticValue visit(Statement p, OutputContext outputContext) {
      AttributesTable table = new AttributesTable(originalTable);
      p.xwhereclause_.accept(new XWhereClauseVisitor(), table);
      p.xorderclause_.accept(new XOrderClauseVisitor(), table);
      Context context = new Context(outputContext, new InputContext(table));
      RList<AttributeValue> result = new RList<>();
      for (XSelectItem x : p.listxselectitem_) {
        result.add(Optional.fromNullable(x.accept(new XSelectItemVisitor(), context).or(null)));
      }
      return result;
    }
  }

  private class XWhereClauseVisitor implements XWhereClause.Visitor<Void, AttributesTable> {
    @SuppressWarnings("unchecked")
    @Override
    public Void visit(WhereClause p, AttributesTable table) {
      Iterator<AttributesRow> rows = table.iterator();
      while (rows.hasNext()) {
        AttributeValue value;
        try {
          value = p.xexpression_.accept(new XExpressionVisitor(), new InputContext(rows.next()))
              .getSingle().or(new CABoolean(false));
        } catch (SemanticValueCastException e) {
          throw new EvaluationException("WHERE expression result is not a single value");
        }
        if (!(value instanceof CABoolean)) {
          throw new EvaluationException("WHERE expression result is not a boolean");
        }
        if (!((CABoolean) value).getValue()) {
          rows.remove();
        }
      }
      return null;
    }

    @Override
    public Void visit(WhereClauseEmpty p, AttributesTable input) {
      return null;
    }
  }

  private class XOrderClauseVisitor implements XOrderClause.Visitor<Void, AttributesTable> {
    @Override
    public Void visit(OrderClause p, AttributesTable table) {
      for (XOrderItem x : p.listxorderitem_) {
        x.accept(new XOrderItemVisitor(), table);
      }
      return null;
    }

    @Override
    public Void visit(OrderClauseEmpty p, AttributesTable arg) {
      return null;
    }
  }

  private class XOrderItemVisitor implements XOrderItem.Visitor<Void, AttributesTable> {
    @Override
    public Void visit(final OrderItemCond p, AttributesTable table) {
      Ordering<AttributesRow> ordering = p.xnullsoption_.accept(new XNullsOptionVisitor(),
          p.xorderoption_.accept(new XOrderOptionVisitor(), null))
          .onResultOf(new Function<AttributesRow, AttributeValue>() {
            @SuppressWarnings("unchecked")
            @Override
            public AttributeValue apply(AttributesRow row) {
              try {
                InputContext inputContext = new InputContext(row);
                return p.xexpression_.accept(new XExpressionVisitor(), inputContext).getSingle()
                    .or(null);
              } catch (SemanticValueCastException e) {
                throw new EvaluationException("ORDER item result is not a single value");
              }
            }
          });
      Collections.sort(table, ordering);
      return null;
    }
  }

  private class XOrderOptionVisitor
      implements XOrderOption.Visitor<Ordering<AttributeValue>, Void> {
    @Override
    public Ordering<AttributeValue> visit(OrderOptionAsc p, Void unused) {
      return Ordering.natural();
    }

    @Override
    public Ordering<AttributeValue> visit(OrderOptionDesc p, Void unused) {
      return Ordering.natural().reverse();
    }

    @Override
    public Ordering<AttributeValue> visit(OrderOptionEmpty p, Void unused) {
      return Ordering.natural();
    }
  }

  private class XNullsOptionVisitor
      implements XNullsOption.Visitor<Ordering<AttributeValue>, Ordering<AttributeValue>> {
    @Override
    public Ordering<AttributeValue> visit(NullsOptionFirst p, Ordering<AttributeValue> ordering) {
      return ordering.nullsFirst();
    }

    @Override
    public Ordering<AttributeValue> visit(NullsOptionLast p, Ordering<AttributeValue> ordering) {
      return ordering.nullsLast();
    }

    @Override
    public Ordering<AttributeValue> visit(NullsOptionEmpty p, Ordering<AttributeValue> ordering) {
      return ordering.nullsFirst();
    }
  }

  private class XSelectItemVisitor implements XSelectItem.Visitor<RSingle, Context> {
    @Override
    public RSingle visit(SelectItem p, Context context) {
      try {
        return p.xexpression_.accept(new XExpressionVisitor(), context.input).getSingle();
      } catch (SemanticValueCastException e) {
        throw new EvaluationException("SELECT ITEM must evaluate to single value");
      }
    }

    @Override
    public RSingle visit(SelectItemAs p, Context context) {
      RSingle value = visit(new SelectItem(p.xexpression_), context);
      context.output.put(p.xident_, value);
      return value;
    }
  }

  private class XExpressionVisitor implements XExpression.Visitor<SemanticValue, InputContext> {
    public SemanticValue visit(CondExprOr p, InputContext context) {
      p.xexpression_1.accept(new XExpressionVisitor(), context);
      p.xexpression_2.accept(new XExpressionVisitor(), context);
      return null;
    }

    public SemanticValue visit(CondExprAnd p, InputContext context) {
      p.xexpression_1.accept(new XExpressionVisitor(), context);
      p.xexpression_2.accept(new XExpressionVisitor(), context);
      return null;
    }

    public SemanticValue visit(CondExprNot p, InputContext context) {
      p.xexpression_.accept(new XExpressionVisitor(), context);
      return null;
    }

    public SemanticValue visit(BoolExprRegex p, InputContext context) {
      p.xexpression_.accept(new XExpressionVisitor(), context);
      //p.string_;
      return null;
    }

    public SemanticValue visit(BoolExprRel p, InputContext context) {
      p.xexpression_1.accept(new XExpressionVisitor(), context);
      p.xrelop_.accept(new XRelOpVisitor(), context);
      p.xexpression_2.accept(new XExpressionVisitor(), context);
      return null;
    }

    public SemanticValue visit(ArithExprAdd p, InputContext context) {
      p.xexpression_1.accept(new XExpressionVisitor(), context);
      p.xarithopadd_.accept(new XArithOpAddVisitor(), context);
      p.xexpression_2.accept(new XExpressionVisitor(), context);
      return null;
    }

    public SemanticValue visit(ArithExprMultiply p, InputContext context) {
      p.xexpression_1.accept(new XExpressionVisitor(), context);
      p.xarithopmultiply_.accept(new XArithOpMultiplyVisitor(), context);
      p.xexpression_2.accept(new XExpressionVisitor(), context);
      return null;
    }

    public SemanticValue visit(ArithExprNeg p, InputContext context) {
      p.xexpression_.accept(new XExpressionVisitor(), context);
      return null;
    }

    public SemanticValue visit(BasicExprVar p, InputContext context) {
      //p.xident_;
      return null;
    }

    public SemanticValue visit(BasicExprCall p, InputContext context) {
      //p.xident_;
      for (XExpression x : p.listxexpression_) {
      }
      return null;
    }

    public SemanticValue visit(BasicExprString p, InputContext context) {
      //p.string_;
      return null;
    }

    public SemanticValue visit(BasicExprTrue p, InputContext context) {
      p.xboolconst_.accept(new XBoolConstVisitor(), context);
      return null;
    }

    public SemanticValue visit(BasicExprInt p, InputContext context) {
      return null;
    }

    public SemanticValue visit(BasicExprDouble p, InputContext context) {
      //p.double_;
      return null;
    }

    public SemanticValue visit(BasicExprBraces p, InputContext context) {
      return null;
    }

    public SemanticValue visit(BasicExprBrackets p, InputContext context) {
      return null;
    }

    public SemanticValue visit(BasicExprAngle p, InputContext context) {
      for (XExpression x : p.listxexpression_) {
      }
      return null;
    }

    @Override
    public SemanticValue visit(BasicExprStmt p, InputContext context) {
      try {
        return p.xstatement_.accept(new XStatementVisitor(), new InnerSelectOutputContext())
            .getSingle();
      } catch (SemanticValueCastException e) {
        throw new EvaluationException("INNER SELECT must return a single value");
      }
    }
  }

  private class XArithOpAddVisitor implements XArithOpAdd.Visitor<SemanticValue, InputContext> {
    public SemanticValue visit(ArithOpAdd p, InputContext context) {
      return null;
    }

    public SemanticValue visit(ArithOpSubstract p, InputContext context) {
      return null;
    }
  }

  private class XArithOpMultiplyVisitor
      implements XArithOpMultiply.Visitor<SemanticValue, InputContext> {
    public SemanticValue visit(ArithOpMultiply p, InputContext context) {
      return null;
    }

    public SemanticValue visit(ArithOpDivide p, InputContext context) {
      return null;
    }

    public SemanticValue visit(ArithOpModulo p, InputContext context) {
      return null;
    }
  }

  private class XRelOpVisitor implements XRelOp.Visitor<SemanticValue, InputContext> {
    public SemanticValue visit(RelOpEqual p, InputContext context) {
      return null;
    }

    public SemanticValue visit(RelOpNotEqual p, InputContext context) {
      return null;
    }

    public SemanticValue visit(RelOpGreater p, InputContext context) {
      return null;
    }

    public SemanticValue visit(RelOpGreaterEqual p, InputContext context) {
      return null;
    }

    public SemanticValue visit(RelOpLesser p, InputContext context) {
      return null;
    }

    public SemanticValue visit(RelOpLesserEqual p, InputContext context) {
      return null;
    }
  }

  private class XBoolConstVisitor implements XBoolConst.Visitor<SemanticValue, InputContext> {
    @Override
    public RSingle<CABoolean> visit(BoolConstTrue p, InputContext context) {
      return new RSingle<>(new CABoolean(true));
    }

    @Override
    public RSingle<CABoolean> visit(BoolConstFalse p, InputContext context) {
      return new RSingle<>(new CABoolean(false));
    }
  }
}
