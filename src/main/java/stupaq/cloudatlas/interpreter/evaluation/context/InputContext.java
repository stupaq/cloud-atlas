package stupaq.cloudatlas.interpreter.evaluation.context;

import com.google.common.base.Optional;

import java.util.HashMap;
import java.util.Map;

import stupaq.cloudatlas.attribute.AttributeName;
import stupaq.cloudatlas.attribute.AttributeValue;
import stupaq.cloudatlas.interpreter.evaluation.data.AttributesRow;
import stupaq.cloudatlas.interpreter.evaluation.data.AttributesTable;
import stupaq.cloudatlas.interpreter.errors.EvaluationException;
import stupaq.cloudatlas.interpreter.values.RCollection;
import stupaq.cloudatlas.interpreter.values.RSingle;
import stupaq.cloudatlas.interpreter.values.SemanticValue;

public class InputContext {
  private final Map<AttributeName, SemanticValue> inputAttributes;

  public InputContext(AttributesRow row) {
    inputAttributes = new HashMap<>();
    for (Map.Entry<AttributeName, Optional<AttributeValue>> attribute : row.entrySet()) {
      inputAttributes.put(attribute.getKey(), new RSingle<>(attribute.getValue()));
    }
  }

  public InputContext(AttributesTable table) {
    Map<AttributeName, RCollection<AttributeValue>> columns = new HashMap<>();
    for (AttributesRow row : table) {
      for (Map.Entry<AttributeName, Optional<AttributeValue>> attribute : row.entrySet()) {
        RCollection<AttributeValue> collection = columns.get(attribute.getKey());
        if (collection == null) {
          collection = new RCollection<>();
          columns.put(attribute.getKey(), collection);
        }
        collection.add(attribute.getValue());
      }
    }
    inputAttributes = new HashMap<>();
    inputAttributes.putAll(columns);
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
