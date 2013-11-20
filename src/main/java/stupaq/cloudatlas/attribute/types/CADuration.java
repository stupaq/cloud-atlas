package stupaq.cloudatlas.attribute.types;

import org.apache.commons.lang.time.DurationFormatUtils;

import stupaq.cloudatlas.attribute.AttributeValue;
import stupaq.cloudatlas.interpreter.semantics.ConvertibleValue;
import stupaq.cloudatlas.interpreter.semantics.ConvertibleValue.ConvertibleValueDefault;
import stupaq.cloudatlas.interpreter.semantics.OperableValue;
import stupaq.cloudatlas.interpreter.semantics.OperableValue.OperableValueDefault;
import stupaq.cloudatlas.interpreter.semantics.RelationalValue;
import stupaq.cloudatlas.interpreter.semantics.RelationalValue.RelationalValueDefault;

public class CADuration extends AbstractLongBacked {
  public CADuration() {
    this(null);
  }

  public CADuration(Long value) {
    super(value);
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
      return new CAInteger(isNull() ? null : get());
    }

    @Override
    public CAString String() {
      return new CAString(isNull() ? null : (get() >= 0 ? "+" : "-") + DurationFormatUtils
          .formatDuration(Math.abs(get()), "d HH:mm:ss.SSS"));
    }
  }

  private class OperableImplementation extends OperableValueDefault {
    @Override
    public AttributeValue zero() {
      return new CADuration(0L);
    }

    @Override
    public AttributeValue add(AttributeValue value) {
      return value.op().addTo(CADuration.this);
    }

    @Override
    public AttributeValue addTo(CADuration value) {
      return new CADuration(isNull(value) ? null : value.get() + get());
    }

    @Override
    public AttributeValue addTo(CATime value) {
      return new CATime(isNull(value) ? null : value.get() + get());
    }

    @Override
    public AttributeValue negate() {
      return new CADuration(isNull() ? null : -get());
    }

    @Override
    public AttributeValue multiply(AttributeValue value) {
      return value.op().multiplyBy(CADuration.this);
    }

    @Override
    public AttributeValue multiplyBy(CADouble value) {
      return new CADuration(isNull(value) ? null : (long) (value.get() * (double) get()));
    }

    @Override
    public AttributeValue multiplyBy(CADuration value) {
      return new CADuration(isNull(value) ? null : value.get() * get());
    }

    @Override
    public AttributeValue multiplyBy(CAInteger value) {
      return new CADuration(isNull(value) ? null : value.get() * get());
    }
  }

  private class RelationalImplementation extends RelationalValueDefault {
    @Override
    public CABoolean lesserThan(AttributeValue value) {
      return value.rel().greaterThan(CADuration.this);
    }

    @Override
    public CABoolean greaterThan(CADuration value) {
      return new CABoolean(isNull(value) ? null : get().compareTo(value.get()) > 0);
    }

    @Override
    public CABoolean equalsTo(CADuration value) {
      return new CABoolean(isNull(value) ? null : get().equals(value.get()));
    }

    @Override
    public CABoolean equalsTo(AttributeValue value) {
      return value.rel().equalsTo(CADuration.this);
    }
  }
}
