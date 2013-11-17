package stupaq.cloudatlas.attribute.types;

import org.apache.commons.lang.time.DurationFormatUtils;

import stupaq.cloudatlas.attribute.AttributeValue;
import stupaq.cloudatlas.interpreter.semantics.ConvertibleValue;
import stupaq.cloudatlas.interpreter.semantics.ConvertibleValue.ConvertibleValueDefault;
import stupaq.cloudatlas.interpreter.semantics.OperableValue;
import stupaq.cloudatlas.interpreter.semantics.OperableValue.OperableValueDefault;
import stupaq.cloudatlas.interpreter.semantics.RelationalValue;
import stupaq.cloudatlas.interpreter.semantics.RelationalValue.RelationalValueDefault;
import stupaq.cloudatlas.serialization.SerializationOnly;

public class CADuration extends AbstractLongValue {
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
    public AttributeValue add(AttributeValue value) {
      return value.op().addTo(CADuration.this);
    }

    @Override
    public AttributeValue addTo(CADuration value) {
      return new CADuration(value.getValue() + getValue());
    }

    @Override
    public AttributeValue addTo(CATime value) {
      return new CATime(value.getValue() + getValue());
    }

    @Override
    public AttributeValue negate() {
      return new CADuration(-getValue());
    }

    @Override
    public AttributeValue multiply(AttributeValue value) {
      return value.op().multiplyBy(CADuration.this);
    }

    @Override
    public AttributeValue multiplyBy(CADouble value) {
      return new CADuration((long) (value.getValue() * (double) getValue()));
    }

    @Override
    public AttributeValue multiplyBy(CADuration value) {
      return new CADuration(value.getValue() * getValue());
    }

    @Override
    public AttributeValue multiplyBy(CAInteger value) {
      return new CADuration(value.getValue() * getValue());
    }
  }

  private class RelationalImplementation extends RelationalValueDefault {
    @Override
    public CABoolean lesserThan(AttributeValue value) {
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
    public CABoolean equalsTo(AttributeValue value) {
      return value.rel().equalsTo(CADuration.this);
    }
  }
}
