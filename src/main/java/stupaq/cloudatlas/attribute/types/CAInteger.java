package stupaq.cloudatlas.attribute.types;

import stupaq.cloudatlas.interpreter.Value;
import stupaq.cloudatlas.interpreter.semantics.ConvertibleValue;
import stupaq.cloudatlas.interpreter.semantics.ConvertibleValue.ConvertibleValueDefault;
import stupaq.cloudatlas.interpreter.semantics.OperableValue;
import stupaq.cloudatlas.interpreter.semantics.OperableValue.OperableValueDefault;
import stupaq.cloudatlas.serialization.SerializationOnly;

public class CAInteger extends LongStub {
  @SerializationOnly
  public CAInteger() {
    this(0L);
  }

  public CAInteger(long value) {
    super(value);
  }

  @Override
  public Class<CAInteger> getType() {
    return CAInteger.class;
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
      return new CADouble(getValue().doubleValue());
    }

    @Override
    public CADuration Duration() {
      return new CADuration(getValue());
    }

    @Override
    public CAInteger Integer() {
      return CAInteger.this;
    }

    @Override
    public CAString String() {
      return new CAString(String.valueOf(getValue()));
    }
  }

  private class OperableImplementation extends OperableValueDefault {
    @Override
    public Value add(Value value) {
      return value.operate().addTo(CAInteger.this);
    }

    @Override
    public Value addTo(CADouble value) {
      return new CADouble(value.getValue() + (double) getValue());
    }

    @Override
    public Value addTo(CAInteger value) {
      return new CAInteger(value.getValue() + getValue());
    }

    @Override
    public Value negate() {
      return new CAInteger(-getValue());
    }

    @Override
    public Value multiply(Value value) {
      return value.operate().multiplyBy(CAInteger.this);
    }

    @Override
    public Value multiplyBy(CADouble value) {
      return new CADouble(value.getValue() * (double) getValue());
    }

    @Override
    public Value multiplyBy(CAInteger value) {
      return new CAInteger(value.getValue() * getValue());
    }

    @Override
    public Value inverse() {
      return new CADouble(1 / (double) getValue());
    }

    @Override
    public Value modulo(Value value) {
      return value.operate().remainderOf(CAInteger.this);
    }

    @Override
    public Value remainderOf(CAInteger value) {
      return new CAInteger(value.getValue() % getValue());
    }
  }
}
