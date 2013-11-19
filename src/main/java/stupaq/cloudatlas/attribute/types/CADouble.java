package stupaq.cloudatlas.attribute.types;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import javax.annotation.Nonnull;

import stupaq.cloudatlas.attribute.AttributeValue;
import stupaq.cloudatlas.interpreter.semantics.ConvertibleValue;
import stupaq.cloudatlas.interpreter.semantics.ConvertibleValue.ConvertibleValueDefault;
import stupaq.cloudatlas.interpreter.semantics.OperableValue;
import stupaq.cloudatlas.interpreter.semantics.OperableValue.OperableValueDefault;
import stupaq.cloudatlas.interpreter.semantics.RelationalValue;
import stupaq.cloudatlas.interpreter.semantics.RelationalValue.RelationalValueDefault;
import stupaq.cloudatlas.serialization.SerializationOnly;
import stupaq.guava.base.PrimitiveWrapper;

public class CADouble extends PrimitiveWrapper<Double> implements AttributeValue {
  @SerializationOnly
  public CADouble() {
    this(0D);
  }

  public CADouble(double value) {
    super(value);
  }

  @Nonnull
  @Override
  public Double get() {
    return super.get();
  }

  @Override
  public void readFields(ObjectInput in) throws IOException, ClassNotFoundException {
    set(in.readDouble());
  }

  @Override
  public void writeFields(ObjectOutput out) throws IOException {
    out.writeDouble(get());
  }

  @Override
  public Class<CADouble> getType() {
    return CADouble.class;
  }

  @Override
  public RelationalValue rel() {
    return new RelationalImplementation();
  }

  @Override
  public int compareTo(AttributeValue o) {
    TypeUtils.assertSameType(this, o);
    return get().compareTo(((CADouble) o).get());
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
    public CADouble Double() {
      return CADouble.this;
    }

    @Override
    public CAInteger Integer() {
      return new CAInteger(get().longValue());
    }

    @Override
    public CAString String() {
      return new CAString(String.valueOf(get()));
    }
  }

  private class OperableImplementation extends OperableValueDefault {
    @Override
    public AttributeValue add(AttributeValue value) {
      return value.op().addTo(CADouble.this);
    }

    @Override
    public AttributeValue addTo(CADouble value) {
      return new CADouble(value.get() + get());
    }

    @Override
    public AttributeValue addTo(CAInteger value) {
      return new CADouble(value.get() + get());
    }

    @Override
    public AttributeValue negate() {
      return new CADouble(-get());
    }

    @Override
    public AttributeValue multiply(AttributeValue value) {
      return value.op().multiplyBy(CADouble.this);
    }

    @Override
    public AttributeValue multiplyBy(CADouble value) {
      return new CADouble(value.get() * get());
    }

    @Override
    public AttributeValue multiplyBy(CADuration value) {
      return new CADuration((long) (value.get() * get()));
    }

    @Override
    public AttributeValue multiplyBy(CAInteger value) {
      return new CADouble(value.get() * get());
    }

    @Override
    public AttributeValue inverse() {
      return new CADouble(1 / get());
    }

    @Override
    public AttributeValue round() {
      return new CADouble((double) Math.round(get()));
    }

    @Override
    public AttributeValue ceil() {
      return new CADouble(Math.ceil(get()));
    }

    @Override
    public AttributeValue floor() {
      return new CADouble(Math.floor(get()));
    }
  }

  private class RelationalImplementation extends RelationalValueDefault {
    @Override
    public CABoolean lesserThan(AttributeValue value) {
      return value.rel().greaterThan(CADouble.this);
    }

    @Override
    public CABoolean greaterThan(CADouble value) {
      return new CABoolean(CADouble.this.get().compareTo(value.get()) > 0);
    }

    @Override
    public CABoolean equalsTo(CADouble value) {
      return new CABoolean(CADouble.this.get().equals(value.get()));
    }

    @Override
    public CABoolean equalsTo(AttributeValue value) {
      return value.rel().equalsTo(CADouble.this);
    }
  }
}
