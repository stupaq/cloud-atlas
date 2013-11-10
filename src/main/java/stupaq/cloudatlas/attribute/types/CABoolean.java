package stupaq.cloudatlas.attribute.types;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import stupaq.cloudatlas.PrimitiveWrapper;
import stupaq.cloudatlas.attribute.AttributeValue;
import stupaq.cloudatlas.interpreter.Value;
import stupaq.cloudatlas.interpreter.semantics.ConvertibleValue;
import stupaq.cloudatlas.interpreter.semantics.ConvertibleValue.ConvertibleValueDefault;
import stupaq.cloudatlas.interpreter.semantics.OperableValue;
import stupaq.cloudatlas.interpreter.semantics.OperableValue.OperableValueDefault;
import stupaq.cloudatlas.serialization.SerializationOnly;

public class CABoolean extends PrimitiveWrapper<Boolean> implements AttributeValue {
  @SerializationOnly
  public CABoolean() {
    this(false);
  }

  public CABoolean(boolean value) {
    super(value);
  }

  @Override
  public void readFields(ObjectInput in) throws IOException, ClassNotFoundException {
    setValue(in.readBoolean());
  }

  @Override
  public void writeFields(ObjectOutput out) throws IOException {
    out.writeBoolean(getValue());
  }

  @Override
  public Class<CABoolean> getType() {
    return CABoolean.class;
  }

  @Override
  public ConvertibleValue to() {
    return new ConvertibleImplementation();
  }

  @Override
  public OperableValue operate() {
    return new OperableImplementation();
  }

  private class ConvertibleImplementation extends ConvertibleValueDefault {
    @Override
    public CABoolean Boolean() {
      return CABoolean.this;
    }

    @Override
    public CAString String() {
      return new CAString(String.valueOf(CABoolean.this));
    }
  }

  private class OperableImplementation extends OperableValueDefault {
    @Override
    public Value and(Value value) {
      return value.operate().andWith(CABoolean.this);
    }

    @Override
    public Value andWith(CABoolean value) {
      return new CABoolean(value.getValue() && getValue());
    }

    @Override
    public Value or(Value value) {
      return value.operate().orWith(CABoolean.this);
    }

    @Override
    public Value orWith(CABoolean value) {
      return new CABoolean(value.getValue() || getValue());
    }

    @Override
    public Value contradiction() {
      return new CABoolean(!getValue());
    }
  }
}
