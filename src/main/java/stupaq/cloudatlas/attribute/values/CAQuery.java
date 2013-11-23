package stupaq.cloudatlas.attribute.values;

import com.google.common.base.Preconditions;

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

public class CAQuery extends AbstractAtomic<String> {
  public static final CompactSerializer<CAQuery> SERIALIZER = new CompactSerializer<CAQuery>() {
    @Override
    public CAQuery readInstance(ObjectInput in) throws IOException {
      return new CAQuery(CompactSerializers.String.readInstance(in));
    }

    @Override
    public void writeInstance(ObjectOutput out, CAQuery object) throws IOException {
      CompactSerializers.String.writeInstance(out, object.orNull());
    }
  };

  public CAQuery(String value) {
    super(value);
    Preconditions.checkNotNull(value);
  }

  public String getQueryString() {
    return get();
  }

  @Override
  public final int compareTo(AttributeValue o) {
    throw new UndefinedOperationException("Cannot compare: " + CAQuery.class.getSimpleName());
  }

  @Override
  public ConvertibleValue to() {
    return new ConvertibleValueDefault();
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
    return TypeDescriptor.CAQuery;
  }
}
