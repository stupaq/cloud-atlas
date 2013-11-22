package stupaq.cloudatlas.query.evaluation.context;

import java.util.HashSet;

import stupaq.cloudatlas.attribute.AttributeValue;
import stupaq.cloudatlas.query.errors.EvaluationException;
import stupaq.cloudatlas.query.semantics.values.RSingle;

public interface OutputContext {

  public void put(String attribute, RSingle<? extends AttributeValue> value);

  public void commit();

  public static class InnerSelectOutputContext implements OutputContext {
    @Override
    public void put(String attribute, RSingle<? extends AttributeValue> value) {
      throw new EvaluationException("Cannot set attribute in this context");
    }

    @Override
    public void commit() {
      // no-op
    }
  }

  public static class RedefinitionAwareOutputContext implements OutputContext {
    private final OutputContext outputContext;
    private final HashSet<String> alreadyDefined;

    public RedefinitionAwareOutputContext(OutputContext outputContext) {
      this.outputContext = outputContext;
      this.alreadyDefined = new HashSet<>();
    }

    @Override
    public void put(String name, RSingle<? extends AttributeValue> value) {
      if (!alreadyDefined.add(name)) {
        throw new EvaluationException("Attribute: " + name + " already defined");
      }
      outputContext.put(name, value);
    }

    @Override
    public void commit() {
      outputContext.commit();
    }
  }
}
