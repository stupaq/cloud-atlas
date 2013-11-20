package stupaq.cloudatlas.attribute;

import stupaq.cloudatlas.interpreter.semantics.ConvertibleValue;
import stupaq.cloudatlas.interpreter.semantics.OperableValue;
import stupaq.cloudatlas.interpreter.semantics.RelationalValue;
import stupaq.cloudatlas.interpreter.typecheck.TypeInfo;
import stupaq.cloudatlas.serialization.CompactSerializable;

public interface AttributeValue extends CompactSerializable, Comparable<AttributeValue> {

  public TypeInfo<? extends AttributeValue> getType();

  ConvertibleValue to();

  OperableValue op();

  RelationalValue rel();

  public boolean isNull();
}
