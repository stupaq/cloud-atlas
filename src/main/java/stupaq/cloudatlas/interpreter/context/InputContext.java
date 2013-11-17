package stupaq.cloudatlas.interpreter.context;

import java.util.Map;

import stupaq.cloudatlas.attribute.AttributeName;
import stupaq.cloudatlas.interpreter.data.AttributesRow;
import stupaq.cloudatlas.interpreter.data.AttributesTable;
import stupaq.cloudatlas.interpreter.errors.EvaluationException;
import stupaq.cloudatlas.interpreter.semantics.SemanticValue;

public class InputContext {
  private final Map<AttributeName, SemanticValue> inputAttributes;

  public InputContext(AttributesRow row) {
  }

  public InputContext(AttributesTable table) {
  }

  public SemanticValue get(String attribute) {
    // Attribute value cannot start with reserved prefix
    SemanticValue value = inputAttributes.get(AttributeName.valueOf(attribute));
    if (value == null) {
      throw new EvaluationException("Unknown attribute: " + attribute);
    }
    return value;
  }
}
