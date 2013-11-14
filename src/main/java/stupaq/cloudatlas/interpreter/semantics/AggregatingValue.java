package stupaq.cloudatlas.interpreter.semantics;

import stupaq.cloudatlas.attribute.AttributeValue;
import stupaq.cloudatlas.attribute.types.CABoolean;
import stupaq.cloudatlas.attribute.types.CADouble;
import stupaq.cloudatlas.attribute.types.CAInteger;
import stupaq.cloudatlas.attribute.types.CAList;
import stupaq.cloudatlas.interpreter.errors.OperationNotApplicable;
import stupaq.cloudatlas.interpreter.types.RSingle;

public interface AggregatingValue<Type extends AttributeValue> {

  public SemanticValue<CADouble> avg();

  public SemanticValue<Type> sum();

  public SemanticValue<CAInteger> count();

  public SemanticValue<CAList<Type>> first(int size);

  public SemanticValue<CAList<Type>> last(int size);

  public SemanticValue<CAList<Type>> random(int size);

  public SemanticValue<Type> min();

  public SemanticValue<Type> max();

  public SemanticValue<CABoolean> land();

  public SemanticValue<CABoolean> lor();

  SemanticValue<Type> distinct();

  SemanticValue unfold();

  public static class AggregatingValueDefault<Type extends AttributeValue>
      implements AggregatingValue<Type> {
    private RuntimeException noAggregate(String operation) throws OperationNotApplicable {
      throw new OperationNotApplicable("Aggregation with " + operation + " is not supported");
    }

    @Override
    public RSingle avg() {
      throw noAggregate("avg(...)");
    }

    @Override
    public RSingle sum() {
      throw noAggregate("sum(...)");
    }

    @Override
    public RSingle<CAInteger> count() {
      throw noAggregate("count(...)");
    }

    @Override
    public RSingle<CAList<Type>> first(int size) {
      throw noAggregate("first(...)");
    }

    @Override
    public RSingle<CAList<Type>> last(int size) {
      throw noAggregate("last(...)");
    }

    @Override
    public RSingle<CAList<Type>> random(int size) {
      throw noAggregate("random(...)");
    }

    @Override
    public RSingle<Type> min() {
      throw noAggregate("min(...)");
    }

    @Override
    public RSingle<Type> max() {
      throw noAggregate("max(...)");
    }

    @Override
    public SemanticValue land() {
      throw noAggregate("land(...)");
    }

    @Override
    public SemanticValue lor() {
      throw noAggregate("lor(...)");
    }

    @Override
    public SemanticValue<Type> distinct() {
      throw noAggregate("distinct(...)");
    }

    @Override
    public SemanticValue unfold() {
      throw noAggregate("unfold(...)");
    }
  }
}