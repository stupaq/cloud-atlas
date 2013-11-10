package stupaq.cloudatlas.attribute.types;

import org.apache.commons.lang.time.DurationFormatUtils;

import stupaq.cloudatlas.interpreter.Value;
import stupaq.cloudatlas.interpreter.semantics.ConvertibleValue;
import stupaq.cloudatlas.interpreter.semantics.ConvertibleValue.ConvertibleValueDefault;
import stupaq.cloudatlas.interpreter.semantics.OperableValue;
import stupaq.cloudatlas.interpreter.semantics.OperableValue.OperableValueDefault;
import stupaq.cloudatlas.interpreter.semantics.RelationalValue;
import stupaq.cloudatlas.interpreter.semantics.RelationalValue.RelationalValueDefault;
import stupaq.cloudatlas.serialization.SerializationOnly;

public class CADuration extends LongStub {
  @SerializationOnly
  public CADuration() {
    this(0L);
  }

  public CADuration(long value) {
    super(value);
  }

  @Override
  public Class<CADuration> getType() {
    return CADuration.class;
  }

  @Override
  public RelationalValue rel() {
    return new RelationalImplementation();
  }

  @Override
  public ConvertibleValue to() {
    return new ConvertibleImplementation();
  }

  @Override
  public OperableValue op() {
    return new OperableImplementation();
  }

  private class ConvertibleImplementation extends ConvertibleValueDefault {
    @Override
    public CADuration Duration() {
      return CADuration.this;
    }

    @Override
    public CAInteger Integer() {
      return new CAInteger(getValue());
    }

    @Override
    public CAString String() {
      return new CAString((getValue() >= 0 ? "+" : "-") + DurationFormatUtils
          .formatDuration(Math.abs(getValue()), "d HH:mm:ss.SSS"));
    }
  }

  private class OperableImplementation extends OperableValueDefault {
    @Override
    public Value add(Value value) {
      return value.op().addTo(CADuration.this);
    }

    @Override
    public Value addTo(CADuration value) {
      return new CADuration(value.getValue() + getValue());
    }

    @Override
    public Value addTo(CATime value) {
      return new CATime(value.getValue() + getValue());
    }

    @Override
    public Value negate() {
      return new CADuration(-getValue());
    }
  }

  private class RelationalImplementation extends RelationalValueDefault {
    @Override
    public CABoolean lessThan(Value value) {
      return value.rel().greaterThan(CADuration.this);
    }

    @Override
    public CABoolean greaterThan(CADuration value) {
      return new CABoolean(CADuration.this.getValue().compareTo(value.getValue()) > 0);
    }

    @Override
    public CABoolean equalsTo(CADuration value) {
      return new CABoolean(CADuration.this.getValue().equals(value.getValue()));
    }

    @Override
    public CABoolean equalsTo(Value value) {
      return value.rel().equalsTo(CADuration.this);
    }
  }
}
