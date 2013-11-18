package stupaq.cloudatlas.interpreter;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.collect.Ordering;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

import stupaq.cloudatlas.attribute.AttributeValue;
import stupaq.cloudatlas.attribute.types.CABoolean;
import stupaq.cloudatlas.attribute.types.CADouble;
import stupaq.cloudatlas.attribute.types.CAInteger;
import stupaq.cloudatlas.attribute.types.CAString;
import stupaq.cloudatlas.attribute.types.CATime;
import stupaq.cloudatlas.interpreter.context.Context;
import stupaq.cloudatlas.interpreter.context.InputContext;
import stupaq.cloudatlas.interpreter.context.OutputContext;
import stupaq.cloudatlas.interpreter.context.OutputContext.InnerSelectOutputContext;
import stupaq.cloudatlas.interpreter.context.OutputContext.RedefinitionAwareOutputContext;
import stupaq.cloudatlas.interpreter.data.AttributesRow;
import stupaq.cloudatlas.interpreter.data.AttributesTable;
import stupaq.cloudatlas.interpreter.errors.EvaluationException;
import stupaq.cloudatlas.interpreter.errors.OperationNotApplicable;
import stupaq.cloudatlas.interpreter.semantics.SemanticValue;
import stupaq.cloudatlas.interpreter.semantics.SemanticValue.SemanticValueCastException;
import stupaq.cloudatlas.interpreter.types.RList;
import stupaq.cloudatlas.interpreter.types.RSingle;
import stupaq.cloudatlas.parser.QueryLanguage.Absyn.*;
import stupaq.guava.base.Function1;
import stupaq.guava.base.Function2;

public class EvalVisitor {
  private static final Log LOG = LogFactory.getLog(EvalVisitor.class);
  private final AttributesTable originalTable;

  public EvalVisitor(AttributesTable originalTable) {
    LOG.info(originalTable);
    this.originalTable = originalTable;
  }

  public OutputContext eval(XProgram program, OutputContext outputContext) {
    program.accept(new XProgramVisitor(), outputContext);
    return outputContext;
  }

  @SuppressWarnings("unchecked")
  private <Type extends AttributeValue> Type getAs(AttributeValue value, Class<Type> clazz) {
    if (value == null) {
      throw new EvaluationException("Expected: " + clazz.getSimpleName() + " got NULL");
    }
    if (clazz.isInstance(value)) {
      return (Type) value;
    } else {
      throw new EvaluationException(
          "Expected type: " + clazz.getSimpleName() + " got: " + value.getType().getSimpleName());
    }
  }

  private abstract static class AttributeValueZipper
      extends Function2<AttributeValue, AttributeValue, AttributeValue> {
  }

  private abstract static class AttributeValueMapper
      extends Function1<AttributeValue, AttributeValue> {
  }

  private class XProgramVisitor implements XProgram.Visitor<Void, OutputContext> {
    @Override
    public Void visit(Program p, OutputContext outputContext) {
      for (XStatement x : p.listxstatement_) {
        // We do not allow attribute redefinition within a single SELECT
        x.accept(new XStatementVisitor(), new RedefinitionAwareOutputContext(outputContext));
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
      if (p.listxselectitem_.size() == 1) {
        return p.listxselectitem_.get(0).accept(new XSelectItemVisitor(), context);
      } else {
        RList<AttributeValue> result = new RList<>();
        for (XSelectItem x : p.listxselectitem_) {
          result.add(Optional.fromNullable(x.accept(new XSelectItemVisitor(), context).or(null)));
        }
        return result;
      }
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
        try {
          if (!getAs(value, CABoolean.class).getValue()) {
            rows.remove();
          }
        } catch (EvaluationException e) {
          throw new EvaluationException("WHERE expression result is not a boolean");
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

  @SuppressWarnings("unchecked")
  private class XExpressionVisitor implements XExpression.Visitor<SemanticValue, InputContext> {
    @Override
    public SemanticValue visit(CondExprOr p, InputContext context) {
      return p.xexpression_1.accept(new XExpressionVisitor(), context)
          .zip(p.xexpression_2.accept(new XExpressionVisitor(), context),
              new AttributeValueZipper() {
                @Override
                public AttributeValue apply(AttributeValue value, AttributeValue value2) {
                  return value.op().or(value2);
                }
              });
    }

    @Override
    public SemanticValue visit(CondExprAnd p, InputContext context) {
      return p.xexpression_1.accept(new XExpressionVisitor(), context)
          .zip(p.xexpression_2.accept(new XExpressionVisitor(), context),
              new AttributeValueZipper() {
                @Override
                public AttributeValue apply(AttributeValue value, AttributeValue value2) {
                  return value.op().and(value2);
                }
              });
    }

    @Override
    public SemanticValue visit(CondExprNot p, InputContext context) {
      return p.xexpression_.accept(new XExpressionVisitor(), context)
          .map(new AttributeValueMapper() {
            @Override
            public AttributeValue apply(AttributeValue value) {
              return value.op().not();
            }
          });
    }

    @Override
    public SemanticValue visit(final BoolExprRegex p, InputContext context) {
      return p.xexpression_.accept(new XExpressionVisitor(), context)
          .map(new AttributeValueMapper() {
            @Override
            public AttributeValue apply(AttributeValue value) {
              return value.op().matches(new CAString(p.string_));
            }
          });
    }

    @Override
    public SemanticValue visit(BoolExprRel p, InputContext context) {
      return p.xexpression_1.accept(new XExpressionVisitor(), context)
          .zip(p.xexpression_2.accept(new XExpressionVisitor(), context),
              p.xrelop_.accept(new XRelOpVisitor(), context));
    }

    @Override
    public SemanticValue visit(ArithExprAdd p, InputContext context) {
      return p.xexpression_1.accept(new XExpressionVisitor(), context)
          .zip(p.xexpression_2.accept(new XExpressionVisitor(), context),
              p.xarithopadd_.accept(new XArithOpAddVisitor(), context));
    }

    @Override
    public SemanticValue visit(ArithExprMultiply p, InputContext context) {
      return p.xexpression_1.accept(new XExpressionVisitor(), context)
          .zip(p.xexpression_2.accept(new XExpressionVisitor(), context),
              p.xarithopmultiply_.accept(new XArithOpMultiplyVisitor(), context));
    }

    @Override
    public SemanticValue visit(ArithExprNeg p, InputContext context) {
      return p.xexpression_.accept(new XExpressionVisitor(), context)
          .map(new AttributeValueMapper() {
            @Override
            public AttributeValue apply(AttributeValue value) {
              return value.op().negate();
            }
          });
    }

    @Override
    public SemanticValue visit(BasicExprVar p, InputContext context) {
      return context.get(p.xident_);
    }

    @Override
    public SemanticValue visit(final BasicExprCall p, InputContext context) {
      final ArgumentsList args = new ArgumentsList();
      // We follow greedy evaluation (it doesn't really matter here since our
      // functions need all arguments at all times)
      for (XExpression x : p.listxexpression_) {
        args.add(x.accept(new XExpressionVisitor(), context));
      }
      try {
        switch (p.xident_.toLowerCase()) {
          // Aggregations
          case "avg":
            return args.get(0).aggregate().avg();
          case "sum":
            return args.get(0).aggregate().sum();
          case "count":
            return args.get(0).aggregate().count();
          case "first":
            return args.get(1).aggregate().first(
                getAs(args.get(0).getSingle().or(null), CAInteger.class).getValue().intValue());
          case "last":
            return args.get(1).aggregate().last(
                getAs(args.get(0).getSingle().or(null), CAInteger.class).getValue().intValue());
          case "random":
            return args.get(1).aggregate().random(
                getAs(args.get(0).getSingle().or(null), CAInteger.class).getValue().intValue());
          case "min":
            return args.get(0).aggregate().min();
          case "max":
            return args.get(0).aggregate().max();
          case "land":
            return args.get(0).aggregate().land();
          case "lor":
            return args.get(0).aggregate().lor();
          case "distinct":
            return args.get(0).aggregate().distinct();
          case "unfold":
            return args.get(0).aggregate().unfold();
          // Utility
          case "isnull":
            return args.get(0).isNull();
          case "now":
            return new RSingle<>(CATime.now());
          case "epoch":
            return new RSingle<>(CATime.epoch());
          // Operations mapped over container
          default:
            return args.get(0).map(new AttributeValueMapper() {
              @Override
              public AttributeValue apply(AttributeValue value) {
                switch (p.xident_) {
                  case "round":
                    return value.op().round();
                  case "ceil":
                    return value.op().ceil();
                  case "floor":
                    return value.op().floor();
                  case "size":
                    return value.op().size();
                  // Conversions
                  case "to_boolean":
                    return value.to().Boolean();
                  case "to_contact":
                    return value.to().Contact();
                  case "to_double":
                    return value.to().Double();
                  case "to_duration":
                    return value.to().Duration();
                  case "to_integer":
                    return value.to().Integer();
                  case "to_list":
                    return value.to().List();
                  case "to_set":
                    return value.to().Set();
                  case "to_string":
                    return value.to().String();
                  case "to_time":
                    return value.to().Time();
                  default:
                    // Other operations were served in outer switch
                    throw new EvaluationException("Function: " + p.xident_ + " is not defined");
                }
              }
            });
        }
      } catch (SemanticValueCastException e) {
        throw new EvaluationException("Function parameter must be a single value");
      } finally {
        args.checkUsage();
      }
    }

    @Override
    public SemanticValue visit(BasicExprString p, InputContext context) {
      return new RSingle<>(new CAString(p.string_));
    }

    @Override
    public SemanticValue visit(BasicExprTrue p, InputContext context) {
      return p.xboolconst_.accept(new XBoolConstVisitor(), context);
    }

    @Override
    public SemanticValue visit(BasicExprInt p, InputContext context) {
      return new RSingle<>(new CAInteger(p.integer_));
    }

    @Override
    public SemanticValue visit(BasicExprDouble p, InputContext context) {
      return new RSingle<>(new CADouble(p.double_));
    }

    @Override
    public SemanticValue visit(BasicExprStmt p, InputContext context) {
      try {
        return p.xstatement_.accept(new XStatementVisitor(), new InnerSelectOutputContext())
            .getSingle();
      } catch (SemanticValueCastException e) {
        throw new EvaluationException("Inner SELECT must return a single value");
      }
    }

    private class ArgumentsList extends ArrayList<SemanticValue> {
      private int highestReferenced = -1;

      @Override
      public SemanticValue get(int index) {
        highestReferenced = Math.max(highestReferenced, index);
        if (index >= size()) {
          throw new EvaluationException("Argument: " + index + " is missing");
        }
        return super.get(index);
      }

      public void checkUsage() {
        if (highestReferenced + 1 != size()) {
          throw new EvaluationException("Function applied to too many arguments.");
        }
      }
    }
  }

  private class XArithOpAddVisitor
      implements XArithOpAdd.Visitor<AttributeValueZipper, InputContext> {
    @Override
    public AttributeValueZipper visit(ArithOpAdd p, InputContext context) {
      return new AttributeValueZipper() {
        @Override
        public AttributeValue apply(AttributeValue value, AttributeValue value2) {
          // Note that addition is not defined for CATime but subtraction is,
          // since our arithmetic does not implement dual operators we have to
          // manually check types here.
          if (value instanceof CATime && value2 instanceof CATime) {
            throw new OperationNotApplicable("Cannot add CATime to CATime");
          }
          return value.op().add(value2);
        }
      };
    }

    @Override
    public AttributeValueZipper visit(ArithOpSubstract p, InputContext context) {
      return new AttributeValueZipper() {
        @Override
        public AttributeValue apply(AttributeValue value, AttributeValue value2) {
          return value.op().add(value2.op().negate());
        }
      };
    }
  }

  private class XArithOpMultiplyVisitor
      implements XArithOpMultiply.Visitor<AttributeValueZipper, InputContext> {
    @Override
    public AttributeValueZipper visit(ArithOpMultiply p, InputContext context) {
      return new AttributeValueZipper() {
        @Override
        public AttributeValue apply(AttributeValue value, AttributeValue value2) {
          return value.op().multiply(value2);
        }
      };
    }

    @Override
    public AttributeValueZipper visit(ArithOpDivide p, InputContext context) {
      return new AttributeValueZipper() {
        @Override
        public AttributeValue apply(AttributeValue value, AttributeValue value2) {
          return value.op().multiply(value2.op().inverse());
        }
      };
    }

    @Override
    public AttributeValueZipper visit(ArithOpModulo p, InputContext context) {
      return new AttributeValueZipper() {
        @Override
        public AttributeValue apply(AttributeValue value, AttributeValue value2) {
          return value.op().modulo(value2);
        }
      };
    }
  }

  private class XRelOpVisitor implements XRelOp.Visitor<AttributeValueZipper, InputContext> {
    @Override
    public AttributeValueZipper visit(RelOpEqual p, InputContext context) {
      return new AttributeValueZipper() {
        @Override
        public AttributeValue apply(AttributeValue value, AttributeValue value2) {
          return value.rel().equalsTo(value2);
        }
      };
    }

    @Override
    public AttributeValueZipper visit(RelOpNotEqual p, InputContext context) {
      return new AttributeValueZipper() {
        @Override
        public AttributeValue apply(AttributeValue value, AttributeValue value2) {
          return value.rel().equalsTo(value2).op().not();
        }
      };
    }

    @Override
    public AttributeValueZipper visit(RelOpGreater p, InputContext context) {
      return new AttributeValueZipper() {
        @Override
        public AttributeValue apply(AttributeValue value, AttributeValue value2) {
          return value2.rel().lesserThan(value);
        }
      };
    }

    @Override
    public AttributeValueZipper visit(RelOpGreaterEqual p, InputContext context) {
      return new AttributeValueZipper() {
        @Override
        public AttributeValue apply(AttributeValue value, AttributeValue value2) {
          return value.rel().lesserThan(value2).op().not();
        }
      };
    }

    @Override
    public AttributeValueZipper visit(RelOpLesser p, InputContext context) {
      return new AttributeValueZipper() {
        @Override
        public AttributeValue apply(AttributeValue value, AttributeValue value2) {
          return value.rel().lesserThan(value2);
        }
      };
    }

    @Override
    public AttributeValueZipper visit(RelOpLesserEqual p, InputContext context) {
      return new AttributeValueZipper() {
        @Override
        public AttributeValue apply(AttributeValue value, AttributeValue value2) {
          return value.rel().lesserOrEqual(value2);
        }
      };
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
