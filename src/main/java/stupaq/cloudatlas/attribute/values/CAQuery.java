package stupaq.cloudatlas.attribute.values;

import com.google.common.base.Preconditions;

import java.io.IOException;

import javax.annotation.concurrent.Immutable;

import stupaq.cloudatlas.attribute.AttributeValue;
import stupaq.cloudatlas.query.errors.UndefinedOperationException;
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
public final class CAQuery extends AbstractAtomic<String> {
  public static final CompactSerializer<CAQuery> SERIALIZER = new CompactSerializer<CAQuery>() {
    @Override
    public CAQuery readInstance(CompactInput in) throws IOException {
      return new CAQuery(CompactSerializers.String.readInstance(in));
    }

    @Override
    public void writeInstance(CompactOutput out, CAQuery object) throws IOException {
      CompactSerializers.String.writeInstance(out, object.orNull());
    }
  };
  private static final long serialVersionUID = 1L;

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
    return TypeDescriptor.CAQuery;
  }

  private class ConvertibleImplementation extends ConvertibleValueDefault {
    @Override
    public CAString String() {
      return new CAString(isNull() ? null : getQueryString());
    }
  }
}
