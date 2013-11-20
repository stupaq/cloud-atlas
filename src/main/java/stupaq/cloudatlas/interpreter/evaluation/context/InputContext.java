package stupaq.cloudatlas.interpreter.evaluation.context;

import java.util.HashMap;
import java.util.Map;

import stupaq.cloudatlas.attribute.AttributeName;
import stupaq.cloudatlas.attribute.AttributeValue;
import stupaq.cloudatlas.interpreter.errors.EvaluationException;
import stupaq.cloudatlas.interpreter.evaluation.data.AttributesRow;
import stupaq.cloudatlas.interpreter.evaluation.data.AttributesTable;
import stupaq.cloudatlas.interpreter.typecheck.TypeInfo;
import stupaq.cloudatlas.interpreter.values.RCollection;
import stupaq.cloudatlas.interpreter.values.RSingle;
import stupaq.cloudatlas.interpreter.values.SemanticValue;

public class InputContext {
  private final Map<AttributeName, SemanticValue> inputAttributes;

  public InputContext(AttributesRow row) {
    inputAttributes = new HashMap<>();
    for (Map.Entry<AttributeName, AttributeValue> attribute : row.entrySet()) {
      inputAttributes.put(attribute.getKey(), new RSingle<>(attribute.getValue()));
    }
  }

  @SuppressWarnings("unchecked")
  public InputContext(AttributesTable table) {
    Map<AttributeName, RCollection<AttributeValue>> columns = new HashMap<>();
    for (AttributesRow row : table) {
      for (Map.Entry<AttributeName, AttributeValue> attribute : row.entrySet()) {
        AttributeName name = attribute.getKey();
        AttributeValue value = attribute.getValue();
        RCollection<AttributeValue> collection = columns.get(name);
        if (collection == null) {
          collection = new RCollection<>((TypeInfo<AttributeValue>) value.getType());
          columns.put(name, collection);
        }
        collection.add(value);
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
