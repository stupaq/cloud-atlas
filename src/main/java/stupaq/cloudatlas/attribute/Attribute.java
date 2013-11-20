package stupaq.cloudatlas.attribute;

import com.google.common.base.Preconditions;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import javax.annotation.Nonnull;

import stupaq.cloudatlas.serialization.CompactSerializable;
import stupaq.cloudatlas.serialization.SerializationOnly;

public final class Attribute<Type extends AttributeValue> implements CompactSerializable {
  @Nonnull
  private final AttributeName name;
  private final Type value;

  public Attribute(@Nonnull AttributeName name, Type value) {
    Preconditions.checkNotNull(name, "AttributeName cannot be null");
    Preconditions.checkNotNull(value, "AttributeValue cannot be null");
    this.name = name;
    this.value = value;
  }

  @SerializationOnly
  public Attribute() {
    name = new AttributeName();
    value = null;
  }

  @Nonnull
  public AttributeName name() {
    return name;
  }

  @Nonnull
  public Type value() {
    return value;
  }

  @Override
  @SuppressWarnings("unchecked")
  public void readFields(ObjectInput in) throws IOException, ClassNotFoundException {
    name.readFields(in);
    value.readFields(in);
  }

  @Override
  public void writeFields(ObjectOutput out) throws IOException {
    name.writeFields(out);
    value.writeFields(out);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Attribute attribute = (Attribute) o;
    return name.equals(attribute.name) && value.equals(attribute.value);

  }

  @Override
  public int hashCode() {
    int result = name.hashCode();
    result = 31 * result + value.hashCode();
    return result;
  }

  @Override
  public String toString() {
    return name + " = " + value;
  }
}
