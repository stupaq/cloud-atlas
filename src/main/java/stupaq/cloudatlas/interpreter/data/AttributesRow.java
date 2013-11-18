package stupaq.cloudatlas.interpreter.data;

import com.google.common.base.Optional;

import java.util.HashMap;

import stupaq.cloudatlas.attribute.AttributeName;
import stupaq.cloudatlas.attribute.AttributeValue;

public class AttributesRow extends HashMap<AttributeName, Optional<AttributeValue>> {
  @Override
  public String toString() {
    // TODO oh God!
    return super.toString().replace("=", " = ").replace("Optional.of(", "")
        .replace("Optional.absent(", "").replace(")", "");
  }
}
