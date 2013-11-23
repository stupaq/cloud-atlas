package stupaq.cloudatlas.attribute.values;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import javax.annotation.concurrent.Immutable;

import stupaq.cloudatlas.attribute.AttributeValue;
import stupaq.cloudatlas.query.semantics.ConvertibleValue;
import stupaq.cloudatlas.query.semantics.ConvertibleValue.ConvertibleValueDefault;
import stupaq.cloudatlas.query.semantics.OperableValue;
import stupaq.cloudatlas.query.semantics.OperableValue.OperableValueDefault;
import stupaq.cloudatlas.query.semantics.RelationalValue;
import stupaq.cloudatlas.query.semantics.RelationalValue.RelationalValueDefault;
import stupaq.compact.CompactSerializer;
import stupaq.compact.CompactSerializers;
import stupaq.compact.TypeDescriptor;

@Immutable
public final class CAInteger extends AbstractAtomic<Long> {
  public static final CompactSerializer<CAInteger> SERIALIZER = new CompactSerializer<CAInteger>() {
    @Override
    public CAInteger readInstance(ObjectInput in) throws IOException {
      return new CAInteger(CompactSerializers.Long.readInstance(in));
    }

    @Override
    public void writeInstance(ObjectOutput out, CAInteger object) throws IOException {
      CompactSerializers.Long.writeInstance(out, object.orNull());
    }
  };

  public CAInteger() {
    super(null);
  }

  public CAInteger(Integer value) {
    super(value == null ? null : value.longValue());
  }

  public CAInteger(Long value) {
    super(value);
  }

  public long getLong() {
    return get();
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
    return TypeDescriptor.CAInteger;
  }

  private class ConvertibleImplementation extends ConvertibleValueDefault {
    @Override
    public CADouble Double() {
      return new CADouble(isNull() ? null : get().doubleValue());
    }

    @Override
    public CADuration Duration() {
      return new CADuration(isNull() ? null : get());
    }

    @Override
    public CAInteger Integer() {
      return CAInteger.this;
    }

    @Override
    public CAString String() {
      return new CAString(isNull() ? null : String.valueOf(get()));
    }
  }

  private class OperableImplementation extends OperableValueDefault {
    @Override
    public AttributeValue zero() {
      return new CAInteger(0);
    }

    @Override
    public AttributeValue add(AttributeValue value) {
      return value.op().addTo(CAInteger.this);
    }

    @Override
    public AttributeValue addTo(CADouble value) {
      return new CADouble(isNull(value) ? null : value.get() + (double) get());
    }

    @Override
    public AttributeValue addTo(CAInteger value) {
      return new CAInteger(isNull(value) ? null : value.get() + get());
    }

    @Override
    public AttributeValue negate() {
      return new CAInteger(isNull() ? null : -get());
    }

    @Override
    public AttributeValue multiply(AttributeValue value) {
      return value.op().multiplyBy(CAInteger.this);
    }

    @Override
    public AttributeValue multiplyBy(CADouble value) {
      return new CADouble(isNull(value) ? null : value.get() * (double) get());
    }

    @Override
    public AttributeValue multiplyBy(CADuration value) {
      return new CADuration(isNull(value) ? null : (long) (value.get() * (double) get()));
    }

    @Override
    public AttributeValue multiplyBy(CAInteger value) {
      return new CAInteger(isNull(value) ? null : value.get() * get());
    }

    @Override
    public CADouble inverse() {
      return new CADouble(isNull() ? null : 1 / (double) get());
    }

    @Override
    public CAInteger modulo(AttributeValue value) {
      return value.op().remainderOf(CAInteger.this);
    }

    @Override
    public CAInteger remainderOf(CAInteger value) {
      return new CAInteger(isNull(value) ? null : value.get() % get());
    }
  }

  private class RelationalImplementation extends RelationalValueDefault {
    @Override
    public CABoolean lesserThan(AttributeValue value) {
      return value.rel().greaterThan(CAInteger.this);
    }

    @Override
    public CABoolean greaterThan(CAInteger value) {
      return new CABoolean(isNull(value) ? null : get().compareTo(value.get()) > 0);
    }

    @Override
    public CABoolean equalsTo(CAInteger value) {
      return new CABoolean(isNull(value) ? null : get().equals(value.get()));
    }

    @Override
    public CABoolean equalsTo(AttributeValue value) {
      return value.rel().equalsTo(CAInteger.this);
    }
  }
}
