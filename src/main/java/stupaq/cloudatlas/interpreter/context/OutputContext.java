package stupaq.cloudatlas.interpreter.context;

import com.google.common.base.Preconditions;

import java.util.HashMap;
import java.util.Map;

import stupaq.cloudatlas.attribute.AttributeName;
import stupaq.cloudatlas.interpreter.errors.EvaluationException;
import stupaq.cloudatlas.interpreter.semantics.SemanticValue;
import stupaq.cloudatlas.interpreter.types.RSingle;

public interface OutputContext {

  public void put(String attribute, SemanticValue value);

  public static class InnerSelectOutputContext implements OutputContext {
    @Override
    public void put(String attribute, SemanticValue value) {
      throw new EvaluationException("Cannot set attribute in this context");
    }
  }

  public static class CollectorOutputContext implements OutputContext {
    private final Map<AttributeName, SemanticValue> outputAttributes;

    public CollectorOutputContext() {
      outputAttributes = new HashMap<>();
    }

    @Override
    public void put(String attribute, SemanticValue value) {
      Preconditions.checkNotNull(attribute);
      Preconditions.checkNotNull(value);
      AttributeName name = AttributeName.valueOf(attribute);
      if (value instanceof RSingle) {
        // Attribute value cannot start with reserved prefix
        if (outputAttributes.containsKey(name)) {
          throw new EvaluationException("Attribute: " + attribute + " already defined");
        }
        outputAttributes.put(name, value);
      } else {
        throw new EvaluationException(
            "Attribute: " + attribute + " value cannot be: " + value.getClass().getSimpleName());
      }
    }
  }
}
