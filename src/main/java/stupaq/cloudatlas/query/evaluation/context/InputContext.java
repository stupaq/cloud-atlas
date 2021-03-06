package stupaq.cloudatlas.query.evaluation.context;

import com.google.common.collect.Maps;

import java.util.Map;

import stupaq.cloudatlas.attribute.AttributeValue;
import stupaq.cloudatlas.naming.AttributeName;
import stupaq.cloudatlas.query.errors.EvaluationException;
import stupaq.cloudatlas.query.evaluation.data.AttributesRow;
import stupaq.cloudatlas.query.evaluation.data.AttributesTable;
import stupaq.cloudatlas.query.semantics.values.RColumn;
import stupaq.cloudatlas.query.semantics.values.RSingle;
import stupaq.cloudatlas.query.semantics.values.SemanticValue;
import stupaq.cloudatlas.query.typecheck.TypeInfo;

public class InputContext {
  private final Map<AttributeName, SemanticValue> inputAttributes;

  public InputContext(AttributesRow row) {
    inputAttributes = Maps.newHashMap();
    for (Map.Entry<AttributeName, AttributeValue> attribute : row.entrySet()) {
      inputAttributes.put(attribute.getKey(), new RSingle<>(attribute.getValue()));
    }
  }

  @SuppressWarnings("unchecked")
  public InputContext(AttributesTable table) {
    Map<AttributeName, RColumn> columns = Maps.newHashMap();
    if (table.isEmpty()) {
      for (Map.Entry<AttributeName, TypeInfo> entry : table.getTypes()) {
        columns.put(entry.getKey(), new RColumn(entry.getValue()));
      }
    } else {
      for (AttributesRow row : table) {
        for (Map.Entry<AttributeName, AttributeValue> attribute : row.entrySet()) {
          AttributeName name = attribute.getKey();
          AttributeValue value = attribute.getValue();
          RColumn collection = columns.get(name);
          if (collection == null) {
            collection = new RColumn<>((TypeInfo<AttributeValue>) value.type());
            columns.put(name, collection);
          }
          collection.add(value);
        }
      }
    }
    assert columns.size() == table.getTypes().size();
    inputAttributes = Maps.newHashMap();
    inputAttributes.putAll(columns);
  }

  public SemanticValue get(String attribute) {
    // Attribute value cannot start with reserved prefix
    SemanticValue value;
    try {
      value = inputAttributes.get(AttributeName.fromString(attribute));
    } catch (IllegalArgumentException e) {
      throw new EvaluationException(e.getMessage());
    }
    if (value == null) {
      throw new EvaluationException("Unknown attribute: " + attribute);
    }
    return value;
  }
}
