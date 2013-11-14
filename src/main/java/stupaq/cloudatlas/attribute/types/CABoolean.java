package stupaq.cloudatlas.attribute.types;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import stupaq.guava.base.PrimitiveWrapper;
import stupaq.cloudatlas.attribute.AttributeValue;
import stupaq.cloudatlas.interpreter.semantics.ConvertibleValue;
import stupaq.cloudatlas.interpreter.semantics.ConvertibleValue.ConvertibleValueDefault;
import stupaq.cloudatlas.interpreter.semantics.OperableValue;
import stupaq.cloudatlas.interpreter.semantics.OperableValue.OperableValueDefault;
import stupaq.cloudatlas.interpreter.semantics.RelationalValue;
import stupaq.cloudatlas.interpreter.semantics.RelationalValue.RelationalValueDefault;
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
  public int compareTo(AttributeValue o) {
    TypeUtils.assertSameType(this, o);
    return getValue().compareTo(((CABoolean) o).getValue());
  }

  @Override
  public ConvertibleValue to() {
    return new ConvertibleImplementation();
  }

  @Override
  public OperableValue op() {
    return new OperableImplementation();
  }

  @Override
  public RelationalValue rel() {
    return new RelationalImplementation();
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
    public AttributeValue and(AttributeValue value) {
      return value.op().andWith(CABoolean.this);
    }

    @Override
    public AttributeValue andWith(CABoolean value) {
      return new CABoolean(value.getValue() && getValue());
    }

    @Override
    public AttributeValue or(AttributeValue value) {
      return value.op().orWith(CABoolean.this);
    }

    @Override
    public AttributeValue orWith(CABoolean value) {
      return new CABoolean(value.getValue() || getValue());
    }

    @Override
    public AttributeValue contradiction() {
      return new CABoolean(!getValue());
    }
  }

  private class RelationalImplementation extends RelationalValueDefault {
    @Override
    public CABoolean equalsTo(AttributeValue value) {
      return value.rel().equalsTo(CABoolean.this);
    }

    @Override
    public CABoolean equalsTo(CABoolean value) {
      return new CABoolean(getValue().equals(value.getValue()));
    }
  }
}
