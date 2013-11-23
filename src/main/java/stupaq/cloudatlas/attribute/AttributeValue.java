package stupaq.cloudatlas.attribute;

import stupaq.cloudatlas.query.semantics.ConvertibleValue;
import stupaq.cloudatlas.query.semantics.OperableValue;
import stupaq.cloudatlas.query.semantics.RelationalValue;
import stupaq.cloudatlas.query.typecheck.TypeInfo;
import stupaq.compact.CompactSerializable;

public interface AttributeValue extends CompactSerializable, Comparable<AttributeValue> {
  public TypeInfo<? extends AttributeValue> type();

  ConvertibleValue to();

  OperableValue op();

  RelationalValue rel();

  public boolean isNull();
}
