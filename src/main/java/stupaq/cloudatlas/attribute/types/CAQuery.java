package stupaq.cloudatlas.attribute.types;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import stupaq.cloudatlas.attribute.AttributeValue;
import stupaq.cloudatlas.interpreter.errors.UndefinedOperationException;
import stupaq.cloudatlas.interpreter.semantics.ConvertibleValue;
import stupaq.cloudatlas.interpreter.semantics.ConvertibleValue.ConvertibleValueDefault;
import stupaq.cloudatlas.interpreter.semantics.OperableValue;
import stupaq.cloudatlas.interpreter.semantics.OperableValue.OperableValueDefault;
import stupaq.cloudatlas.interpreter.semantics.RelationalValue;
import stupaq.cloudatlas.interpreter.semantics.RelationalValue.RelationalValueDefault;
import stupaq.cloudatlas.serialization.SerializationOnly;
import stupaq.guava.base.PrimitiveWrapper;

public class CAQuery extends PrimitiveWrapper<String> implements AttributeValue {
  private static final String NOT_YET_DESERIALIZED = "";

  @SerializationOnly
  public CAQuery() {
    super(NOT_YET_DESERIALIZED);
  }

  public CAQuery(String value) {
    super(value);
  }

  public String getQueryString() {
    return get();
  }

  @Override
  public void readFields(ObjectInput in) throws IOException, ClassNotFoundException {
    set(in.readUTF());
  }

  @Override
  public void writeFields(ObjectOutput out) throws IOException {
    out.writeUTF(get());
  }

  @Override
  public Class<CAQuery> getType() {
    return CAQuery.class;
  }

  @Override
  public int compareTo(AttributeValue o) {
    throw new UndefinedOperationException("Cannot compare: " + getType().getSimpleName());
  }

  @Override
  public ConvertibleValue to() {
    return new ConvertibleValueDefault();
  }

  @Override
  public OperableValue op() {
    return new OperableValueDefault();
  }

  @Override
  public RelationalValue rel() {
    return new RelationalValueDefault();
  }
}
