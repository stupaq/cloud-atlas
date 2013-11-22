package stupaq.cloudatlas.query.interpreter;

import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.collect.Ordering;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

import stupaq.cloudatlas.attribute.AttributeValue;
import stupaq.cloudatlas.attribute.types.CABoolean;
import stupaq.cloudatlas.attribute.types.CADouble;
import stupaq.cloudatlas.attribute.types.CAInteger;
import stupaq.cloudatlas.attribute.types.CAString;
import stupaq.cloudatlas.attribute.types.CATime;
import stupaq.cloudatlas.query.errors.EvaluationException;
import stupaq.cloudatlas.query.errors.TypeCheckerException;
import stupaq.cloudatlas.query.errors.UndefinedOperationException;
import stupaq.cloudatlas.query.evaluation.context.Context;
import stupaq.cloudatlas.query.evaluation.context.InputContext;
import stupaq.cloudatlas.query.evaluation.context.OutputContext;
import stupaq.cloudatlas.query.evaluation.context.OutputContext.InnerSelectOutputContext;
import stupaq.cloudatlas.query.evaluation.context.OutputContext.RedefinitionAwareOutputContext;
import stupaq.cloudatlas.query.evaluation.data.AttributesRow;
import stupaq.cloudatlas.query.evaluation.data.AttributesTable;
import stupaq.cloudatlas.query.semantics.values.RSingle;
import stupaq.cloudatlas.query.semantics.values.SemanticValue;
import stupaq.cloudatlas.query.semantics.values.SemanticValue.SemanticValueCastException;
import stupaq.cloudatlas.query.parser.QueryLanguage.Absyn.*;
import stupaq.guava.base.Function1;
import stupaq.guava.base.Function2;

public class EvalVisitor {
  private final AttributesTable originalTable;

  public EvalVisitor(AttributesTable originalTable) {
    this.originalTable = originalTable;
  }

  public OutputContext eval(XProgram program, OutputContext outputContext) {
    program.accept(new XProgramVisitor(), outputContext);
    return outputContext;
  }

  @SuppressWarnings("unchecked")
  private <Type extends AttributeValue> Type expect(Class<Type> clazz, AttributeValue value) {
    Preconditions.checkNotNull(value);
    if (!clazz.isInstance(value)) {
      throw new TypeCheckerException(
          "Expected type: " + clazz.getSimpleName() + " got: " + value.getType());
    }
    return (Type) value;
  }

  private abstract static class AttributeValueMapper
      extends Function1<AttributeValue, AttributeValue> {
  }

  private abstract static class AttributeValueZipper
      extends Function2<AttributeValue, AttributeValue, AttributeValue> {
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

  private class XStatementVisitor implements XStatement.Visitor<Void, OutputContext> {
    @Override
    public Void visit(Statement p, OutputContext outputContext) {
      AttributesTable table = new AttributesTable(originalTable);
      p.xwhereclause_.accept(new XWhereClauseVisitor(), table);
      p.xorderclause_.accept(new XOrderClauseVisitor(), table);
      Context context = new Context(outputContext, new InputContext(table));
      for (XSelectItem x : p.listxselectitem_) {
        x.accept(new XSelectItemVisitor(), context);
      }
      return null;
    }
  }

  private class XStatementInnerVisitor implements XStatement.Visitor<RSingle, OutputContext> {
    @Override
    public RSingle visit(Statement p, OutputContext outputContext) {
      AttributesTable table = new AttributesTable(originalTable);
      p.xwhereclause_.accept(new XWhereClauseVisitor(), table);
      p.xorderclause_.accept(new XOrderClauseVisitor(), table);
      Context context = new Context(outputContext, new InputContext(table));
      if (p.listxselectitem_.size() == 1) {
        return p.listxselectitem_.get(0).accept(new XSelectItemVisitor(), context);
      } else {
        throw new EvaluationException("Inner SELECT must return a single value");
      }
    }
  }

  private class XWhereClauseVisitor implements XWhereClause.Visitor<Void, AttributesTable> {
    @Override
    public Void visit(WhereClause p, AttributesTable table) {
      Iterator<AttributesRow> rows = table.iterator();
      while (rows.hasNext()) {
        AttributeValue value;
        try {
          value = p.xexpression_.accept(new XExpressionVisitor(), new InputContext(rows.next()))
              .getSingle().get();
          if (!expect(CABoolean.class, value).getOr(false)) {
            rows.remove();
          }
        } catch (EvaluationException e) {
          throw new EvaluationException(
              "WHERE expression result is not a boolean: " + e.getMessage());
        } catch (SemanticValueCastException e) {
          throw new EvaluationException("WHERE expression result is not a single value");
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
                AttributeValue value =
                    p.xexpression_.accept(new XExpressionVisitor(), inputContext).getSingle().get();
                return value.isNull() ? null : value;
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

    @SuppressWarnings("unchecked")
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
            return args.get(1).aggregate()
                .first(expect(CAInteger.class, args.get(0).getSingle().get()));
          case "last":
            return args.get(1).aggregate()
                .last(expect(CAInteger.class, args.get(0).getSingle().get()));
          case "random":
            return args.get(1).aggregate()
                .random(expect(CAInteger.class, args.get(0).getSingle().get()));
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
          case "is_null":
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
      return p.xstatement_.accept(new XStatementInnerVisitor(), new InnerSelectOutputContext());
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
          throw new EvaluationException("Function applied to invalid number of arguments");
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
            throw new UndefinedOperationException("Cannot add CATime to CATime");
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
