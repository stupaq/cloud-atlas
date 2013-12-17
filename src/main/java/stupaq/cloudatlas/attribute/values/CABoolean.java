package stupaq.cloudatlas.attribute.values;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import javax.annotation.concurrent.Immutable;

import stupaq.cloudatlas.attribute.AttributeValue;
import stupaq.cloudatlas.query.errors.UndefinedOperationException;
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
public final class CABoolean extends AbstractAtomic<Boolean> {
  public static final CompactSerializer<CABoolean> SERIALIZER = new CompactSerializer<CABoolean>() {
    @Override
    public CABoolean readInstance(ObjectInput in) throws IOException {
      return new CABoolean(CompactSerializers.Boolean.readInstance(in));
    }

    @Override
    public void writeInstance(ObjectOutput out, CABoolean object) throws IOException {
      CompactSerializers.Boolean.writeInstance(out, object.orNull());
    }
  };

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
  public int compareTo(AttributeValue other) {
    throw new UndefinedOperationException("Cannot compare: " + type());
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

  @Override
  public TypeDescriptor descriptor() {
    return TypeDescriptor.CABoolean;
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
