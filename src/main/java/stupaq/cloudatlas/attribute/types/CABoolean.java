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

public class CABoolean extends PrimitiveWrapper<Boolean> implements AttributeValue {
  @SerializationOnly
  public CABoolean() {
    this(false);
  }

  public CABoolean(boolean value) {
    super(value);
  }

  @Nonnull
  @Override
  public Boolean get() {
    return super.get();
  }

  @Override
  public void readFields(ObjectInput in) throws IOException, ClassNotFoundException {
    set(in.readBoolean());
  }

  @Override
  public void writeFields(ObjectOutput out) throws IOException {
    out.writeBoolean(get());
  }

  @Override
  public Class<CABoolean> getType() {
    return CABoolean.class;
  }

  @Override
  public int compareTo(AttributeValue o) {
    TypeUtils.assertSameType(this, o);
    return get().compareTo(((CABoolean) o).get());
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
      return new CABoolean(value.get() && get());
    }

    @Override
    public CABoolean or(AttributeValue value) {
      return value.op().orWith(CABoolean.this);
    }

    @Override
    public CABoolean orWith(CABoolean value) {
      return new CABoolean(value.get() || get());
    }

    @Override
    public AttributeValue not() {
      return new CABoolean(!get());
    }
  }

  private class RelationalImplementation extends RelationalValueDefault {
    @Override
    public CABoolean equalsTo(AttributeValue value) {
      return value.rel().equalsTo(CABoolean.this);
    }

    @Override
    public CABoolean equalsTo(CABoolean value) {
      return new CABoolean(get().equals(value.get()));
    }
  }
}
