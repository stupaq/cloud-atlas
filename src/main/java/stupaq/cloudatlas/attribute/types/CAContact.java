package stupaq.cloudatlas.attribute.types;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import stupaq.cloudatlas.attribute.AttributeValue;
import stupaq.cloudatlas.interpreter.errors.OperationNotApplicable;
import stupaq.cloudatlas.interpreter.semantics.ConvertibleValue;
import stupaq.cloudatlas.interpreter.semantics.ConvertibleValue.ConvertibleValueDefault;
import stupaq.cloudatlas.interpreter.semantics.OperableValue;
import stupaq.cloudatlas.interpreter.semantics.OperableValue.OperableValueDefault;
import stupaq.cloudatlas.interpreter.semantics.RelationalValue;
import stupaq.cloudatlas.interpreter.semantics.RelationalValue.RelationalValueDefault;
import stupaq.cloudatlas.serialization.SerializationOnly;
import stupaq.guava.base.PrimitiveWrapper;

// TODO: contact has more structure than string, we don't know it right now though
public class CAContact extends PrimitiveWrapper<String> implements AttributeValue {
  private static final String NOT_YET_DESERIALIZED = "";

  @SerializationOnly
  public CAContact() {
    super(NOT_YET_DESERIALIZED);
  }

  public CAContact(String value) {
    super(value);
  }

  @Override
  public void readFields(ObjectInput in) throws IOException, ClassNotFoundException {
    setValue(in.readUTF());
  }

  @Override
  public void writeFields(ObjectOutput out) throws IOException {
    out.writeUTF(getValue());
  }

  @Override
  public Class<CAContact> getType() {
    return CAContact.class;
  }

  @Override
  public int compareTo(AttributeValue o) {
    throw new OperationNotApplicable("Cannot compare: " + getType().getSimpleName());
  }

  @Override
  public ConvertibleValue to() {
    return new ConvertibleImplementation();
  }

  @Override
  public OperableValue op() {
    return new OperableValueDefault();
  }

  @Override
  public RelationalValue rel() {
    return new RelationalValueDefault();
  }

  private class ConvertibleImplementation extends ConvertibleValueDefault {
    @Override
    public CAContact Contact() {
      return CAContact.this;
    }

    @Override
    public CAString String() {
      return new CAString(getValue());
    }
  }
}
