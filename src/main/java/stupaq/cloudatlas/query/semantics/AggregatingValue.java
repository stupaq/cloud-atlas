package stupaq.cloudatlas.query.semantics;

import stupaq.cloudatlas.attribute.AttributeValue;
import stupaq.cloudatlas.attribute.values.CABoolean;
import stupaq.cloudatlas.attribute.values.CAInteger;
import stupaq.cloudatlas.attribute.values.CAList;
import stupaq.cloudatlas.query.errors.UndefinedOperationException;
import stupaq.cloudatlas.query.semantics.values.RSingle;
import stupaq.cloudatlas.query.semantics.values.SemanticValue;

public interface AggregatingValue<Type extends AttributeValue> {

  public SemanticValue avg();

  public SemanticValue<Type> sum();

  public SemanticValue<CAInteger> count();

  public SemanticValue<CAList<Type>> first(CAInteger size);

  public SemanticValue<CAList<Type>> last(CAInteger size);

  public SemanticValue<CAList<Type>> random(CAInteger size);

  public SemanticValue<Type> min();

  public SemanticValue<Type> max();

  public SemanticValue<CABoolean> land();

  public SemanticValue<CABoolean> lor();

  public SemanticValue<Type> distinct();

  public SemanticValue unfold();

  public static class AggregatingValueDefault<Type extends AttributeValue>
      implements AggregatingValue<Type> {
    private RuntimeException noAggregate(String operation) throws UndefinedOperationException {
      throw new UndefinedOperationException("Aggregation with " + operation + " is not supported");
    }

    @Override
    public RSingle avg() {
      throw noAggregate("avg(...)");
    }

    @Override
    public RSingle<Type> sum() {
      throw noAggregate("sum(...)");
    }

    @Override
    public RSingle<CAInteger> count() {
      throw noAggregate("count(...)");
    }

    @Override
    public RSingle<CAList<Type>> first(CAInteger size) {
      throw noAggregate("first(...)");
    }

    @Override
    public RSingle<CAList<Type>> last(CAInteger size) {
      throw noAggregate("last(...)");
    }

    @Override
    public RSingle<CAList<Type>> random(CAInteger size) {
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
    public RSingle<CABoolean> land() {
      throw noAggregate("land(...)");
    }

    @Override
    public RSingle<CABoolean> lor() {
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
