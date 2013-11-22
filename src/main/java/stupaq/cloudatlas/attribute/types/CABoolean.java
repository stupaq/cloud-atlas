package stupaq.cloudatlas.attribute.types;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import stupaq.cloudatlas.attribute.AttributeValue;
import stupaq.cloudatlas.query.errors.UndefinedOperationException;
import stupaq.cloudatlas.query.semantics.ConvertibleValue;
import stupaq.cloudatlas.query.semantics.ConvertibleValue.ConvertibleValueDefault;
import stupaq.cloudatlas.query.semantics.OperableValue;
import stupaq.cloudatlas.query.semantics.OperableValue.OperableValueDefault;
import stupaq.cloudatlas.query.semantics.RelationalValue;
import stupaq.cloudatlas.query.semantics.RelationalValue.RelationalValueDefault;

public class CABoolean extends AbstractAtomic<Boolean> {
  public CABoolean() {
    this(null);
  }

  public CABoolean(Boolean value) {
    super(value);
  }

  public boolean getOr(boolean alternative) {
    return isNull() ? alternative : get();
  }

  @Override
  public void readFields(ObjectInput in) throws IOException, ClassNotFoundException {
    if (in.readBoolean()) {
      set(in.readBoolean());
    }
  }

  @Override
  public void writeFields(ObjectOutput out) throws IOException {
    out.writeBoolean(!isNull());
    if (!isNull()) {
      out.writeBoolean(get());
    }
  }

  @Override
  public int compareTo(AttributeValue other) {
    throw new UndefinedOperationException("Cannot compare: " + getType());
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
      return new CAString(isNull() ? null : String.valueOf(get()));
    }
  }

  private class OperableImplementation extends OperableValueDefault {
    @Override
    public CABoolean or(AttributeValue value) {
      return value.op().orWith(CABoolean.this);
    }

    @Override
    public CABoolean orWith(CABoolean value) {
      return new CABoolean(isNull(value) ? null : value.get() || get());
    }

    @Override
    public CABoolean not() {
      return new CABoolean(isNull() ? null : !get());
    }
  }

  private class RelationalImplementation extends RelationalValueDefault {
    @Override
    public CABoolean equalsTo(AttributeValue value) {
      return value.rel().equalsTo(CABoolean.this);
    }

    @Override
    public CABoolean equalsTo(CABoolean value) {
      return new CABoolean(isNull(value) ? null : get().equals(value.get()));
    }
  }
}
