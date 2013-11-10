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

public class CADouble extends PrimitiveWrapper<Double> implements AttributeValue, Value {
  @SerializationOnly
  public CADouble() {
    this(0D);
  }

  public CADouble(Double value) {
    super(value);
  }

  @Override
  public void readFields(ObjectInput in) throws IOException, ClassNotFoundException {
    setValue(in.readDouble());
  }

  @Override
  public void writeFields(ObjectOutput out) throws IOException {
    out.writeDouble(getValue());
  }

  @Override
  public Class<CADouble> getType() {
    return CADouble.class;
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
    public CADouble Double() {
      return CADouble.this;
    }

    @Override
    public CAInteger Integer() {
      return new CAInteger(getValue().longValue());
    }

    @Override
    public CAString String() {
      return new CAString(String.valueOf(getValue()));
    }
  }

  private class OperableImplementation extends OperableValueDefault {
    @Override
    public Value add(Value value) {
      return value.operate().addTo(CADouble.this);
    }

    @Override
    public Value addTo(CADouble value) {
      return new CADouble(value.getValue() + getValue());
    }

    @Override
    public Value addTo(CAInteger value) {
      return new CADouble(value.getValue() + getValue());
    }

    @Override
    public Value negate() {
      return new CADouble(-getValue());
    }

    @Override
    public Value multiply(Value value) {
      return value.operate().multiplyBy(CADouble.this);
    }

    @Override
    public Value multiplyBy(CADouble value) {
      return new CADouble(value.getValue() * getValue());
    }

    @Override
    public Value multiplyBy(CAInteger value) {
      return new CADouble(value.getValue() * getValue());
    }

    @Override
    public Value inverse() {
      return new CADouble(1 / getValue());
    }

    @Override
    public Value round() {
      return new CADouble((double) Math.round(getValue()));
    }

    @Override
    public Value ceil() {
      return new CADouble(Math.ceil(getValue()));
    }

    @Override
    public Value floor() {
      return new CADouble(Math.floor(getValue()));
    }
  }
}
