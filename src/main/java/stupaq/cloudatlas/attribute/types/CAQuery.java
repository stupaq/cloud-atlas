package stupaq.cloudatlas.attribute.types;

import com.google.common.base.Preconditions;

import stupaq.cloudatlas.attribute.AttributeValue;
import stupaq.cloudatlas.interpreter.errors.UndefinedOperationException;
import stupaq.cloudatlas.interpreter.semantics.ConvertibleValue;
import stupaq.cloudatlas.interpreter.semantics.ConvertibleValue.ConvertibleValueDefault;
import stupaq.cloudatlas.interpreter.semantics.OperableValue;
import stupaq.cloudatlas.interpreter.semantics.OperableValue.OperableValueDefault;
import stupaq.cloudatlas.interpreter.semantics.RelationalValue;
import stupaq.cloudatlas.interpreter.semantics.RelationalValue.RelationalValueDefault;
import stupaq.cloudatlas.serialization.SerializationOnly;

public class CAQuery extends AbstractStringBacked {
  private static final String NOT_DESERIALIZED = "NOT DESERIALIZED";

  @SerializationOnly
  public CAQuery() {
    super(NOT_DESERIALIZED);
  }

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
  public Class<CAQuery> getType() {
    return CAQuery.class;
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
}
