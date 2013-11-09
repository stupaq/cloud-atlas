package stupaq.cloudatlas.attribute;

import stupaq.cloudatlas.interpreter.Value;
import stupaq.cloudatlas.serialization.CompactSerializable;

public interface AttributeValue extends CompactSerializable, Value {

  @Override
  public Class<? extends AttributeValue> getType();
}
