package stupaq.cloudatlas.attribute.values;

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
import stupaq.compact.CompactSerializer;
import stupaq.compact.TypeDescriptor;
import stupaq.compact.CompactSerializers;

public class CAContact extends AbstractAtomic<String> {
  public static final CompactSerializer<CAContact> SERIALIZER = new CompactSerializer<CAContact>() {
    @Override
    public CAContact readInstance(ObjectInput in) throws IOException {
      return new CAContact(CompactSerializers.String.readInstance(in));
    }

    @Override
    public void writeInstance(ObjectOutput out, CAContact object) throws IOException {
      CompactSerializers.String.writeInstance(out, object.orNull());
    }
  };

  public CAContact() {
    super(null);
  }

  public CAContact(String value) {
    super(value);
  }

  @Override
  public int compareTo(AttributeValue other) {
    throw new UndefinedOperationException("Cannot compare: " + CAContact.class.getSimpleName());
  }

  @Override
  public ConvertibleValue to() {
    return new ConvertibleImplementation();
  }

  @Override
  public OperableValue op() {
    return new OperableValueDefault();
  }

  @Override
  public RelationalValue rel() {
    return new RelationalValueDefault();
  }

  @Override
  public TypeDescriptor descriptor() {
    return TypeDescriptor.CAContact;
  }

  private class ConvertibleImplementation extends ConvertibleValueDefault {
    @Override
    public CAContact Contact() {
      return CAContact.this;
    }

    @Override
    public CAString String() {
      return new CAString(isNull() ? null : get());
    }
  }
}
