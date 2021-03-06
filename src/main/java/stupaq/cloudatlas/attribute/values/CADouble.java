package stupaq.cloudatlas.attribute.values;

import java.io.IOException;

import javax.annotation.concurrent.Immutable;

import stupaq.cloudatlas.attribute.AttributeValue;
import stupaq.cloudatlas.query.semantics.ConvertibleValue;
import stupaq.cloudatlas.query.semantics.ConvertibleValue.ConvertibleValueDefault;
import stupaq.cloudatlas.query.semantics.OperableValue;
import stupaq.cloudatlas.query.semantics.OperableValue.OperableValueDefault;
import stupaq.cloudatlas.query.semantics.RelationalValue;
import stupaq.cloudatlas.query.semantics.RelationalValue.RelationalValueDefault;
import stupaq.compact.CompactInput;
import stupaq.compact.CompactOutput;
import stupaq.compact.CompactSerializer;
import stupaq.compact.CompactSerializers;
import stupaq.compact.TypeDescriptor;

@Immutable
public final class CADouble extends AbstractAtomic<Double> {
  public static final CompactSerializer<CADouble> SERIALIZER = new CompactSerializer<CADouble>() {
    @Override
    public CADouble readInstance(CompactInput in) throws IOException {
      return new CADouble(CompactSerializers.Double.readInstance(in));
    }

    @Override
    public void writeInstance(CompactOutput out, CADouble object) throws IOException {
      CompactSerializers.Double.writeInstance(out, object.orNull());
    }
  };

  public CADouble() {
    super(null);
  }

  public CADouble(Integer value) {
    super(value == null ? null : value.doubleValue());
  }

  public CADouble(Long value) {
    super(value == null ? null : value.doubleValue());
  }

  public CADouble(Double value) {
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

  @Override
  public TypeDescriptor descriptor() {
    return TypeDescriptor.CADouble;
  }

  private class ConvertibleImplementation extends ConvertibleValueDefault {
    @Override
    public CADouble Double() {
      return CADouble.this;
    }

    @Override
    public CAInteger Integer() {
      return new CAInteger(isNull() ? null : get().longValue());
    }

    @Override
    public CAString String() {
      return new CAString(isNull() ? null : String.valueOf(get()));
    }
  }

  private class OperableImplementation extends OperableValueDefault {
    @Override
    public AttributeValue zero() {
      return new CADouble(0);
    }

    @Override
    public AttributeValue add(AttributeValue value) {
      return value.op().addTo(CADouble.this);
    }

    @Override
    public AttributeValue addTo(CADouble value) {
      return new CADouble(isNull(value) ? null : value.get() + get());
    }

    @Override
    public AttributeValue addTo(CAInteger value) {
      return new CADouble(isNull(value) ? null : value.get() + get());
    }

    @Override
    public AttributeValue negate() {
      return new CADouble(isNull() ? null : -get());
    }

    @Override
    public AttributeValue multiply(AttributeValue value) {
      return value.op().multiplyBy(CADouble.this);
    }

    @Override
    public AttributeValue multiplyBy(CADouble value) {
      return new CADouble(isNull(value) ? null : value.get() * get());
    }

    @Override
    public AttributeValue multiplyBy(CADuration value) {
      return new CADuration(isNull(value) ? null : (long) (value.get() * get()));
    }

    @Override
    public AttributeValue multiplyBy(CAInteger value) {
      return new CADouble(isNull(value) ? null : value.get() * get());
    }

    @Override
    public CADouble inverse() {
      return new CADouble(isNull() ? null : 1 / get());
    }

    @Override
    public CADouble round() {
      return new CADouble(isNull() ? null : (double) Math.round(get()));
    }

    @Override
    public CADouble ceil() {
      return new CADouble(isNull() ? null : Math.ceil(get()));
    }

    @Override
    public CADouble floor() {
      return new CADouble(isNull() ? null : Math.floor(get()));
    }
  }

  private class RelationalImplementation extends RelationalValueDefault {
    @Override
    public CABoolean lesserThan(AttributeValue value) {
      return value.rel().greaterThan(CADouble.this);
    }

    @Override
    public CABoolean greaterThan(CADouble value) {
      return new CABoolean(isNull(value) ? null : get().compareTo(value.get()) > 0);
    }

    @Override
    public CABoolean equalsTo(CADouble value) {
      return new CABoolean(isNull(value) ? null : get().equals(value.get()));
    }

    @Override
    public CABoolean equalsTo(AttributeValue value) {
      return value.rel().equalsTo(CADouble.this);
    }
  }
}
