package stupaq.cloudatlas.attribute;

import stupaq.cloudatlas.query.semantics.ConvertibleValue;
import stupaq.cloudatlas.query.semantics.OperableValue;
import stupaq.cloudatlas.query.semantics.RelationalValue;
import stupaq.cloudatlas.attribute.types.TypeInfo;
import stupaq.cloudatlas.serialization.CompactSerializable;

public interface AttributeValue extends CompactSerializable, Comparable<AttributeValue> {

  public TypeInfo<? extends AttributeValue> getType();

  ConvertibleValue to();

  OperableValue op();

  RelationalValue rel();

  public boolean isNull();
}
