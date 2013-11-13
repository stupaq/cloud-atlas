package stupaq.cloudatlas.interpreter.semantics;

import stupaq.cloudatlas.interpreter.errors.OperationNotApplicable;

public interface AggregatingValue {

  public SemanticValue avg();

  public SemanticValue sum();

  public SemanticValue count();

  public SemanticValue first(int size);

  public SemanticValue last(int size);

  public SemanticValue random(int size);

  public SemanticValue min();

  public SemanticValue max();

  public SemanticValue land();

  public SemanticValue lor();

  SemanticValue distinct();

  SemanticValue unfold();

  public static class AggregatingValueDefault implements AggregatingValue {
    private SemanticValue noAggregate(String operation) throws OperationNotApplicable {
      throw new OperationNotApplicable("Aggregation with " + operation + " is not supported");
    }

    @Override
    public SemanticValue avg() {
      return noAggregate("avg(...)");
    }

    @Override
    public SemanticValue sum() {
      return noAggregate("sum(...)");
    }

    @Override
    public SemanticValue count() {
      return noAggregate("count(...)");
    }

    @Override
    public SemanticValue first(int size) {
      return noAggregate("first(...)");
    }

    @Override
    public SemanticValue last(int size) {
      return noAggregate("last(...)");
    }

    @Override
    public SemanticValue random(int size) {
      return noAggregate("random(...)");
    }

    @Override
    public SemanticValue min() {
      return noAggregate("min(...)");
    }

    @Override
    public SemanticValue max() {
      return noAggregate("max(...)");
    }

    @Override
    public SemanticValue land() {
      return noAggregate("land(...)");
    }

    @Override
    public SemanticValue lor() {
      return noAggregate("lor(...)");
    }

    @Override
    public SemanticValue distinct() {
      return noAggregate("distinct(...)");
    }

    @Override
    public SemanticValue unfold() {
      return noAggregate("unfold(...)");
    }
  }
}
