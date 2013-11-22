package stupaq.cloudatlas.query.evaluation.data;

import java.util.HashMap;

import stupaq.cloudatlas.attribute.AttributeName;
import stupaq.cloudatlas.attribute.AttributeValue;

public class AttributesRow extends HashMap<AttributeName, AttributeValue> {
  @Override
  public String toString() {
    // TODO oh God!
    return super.toString().replace("=", " = ");
  }
}
