package stupaq.cloudatlas.attribute;

import com.google.common.base.Preconditions;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import stupaq.cloudatlas.serialization.CompactSerializable;
import stupaq.cloudatlas.serialization.SerializationOnly;

public final class Attribute<Type> implements CompactSerializable {
  private final AttributeName name;
  private AttributeType type;
  private Type value;

  @SerializationOnly
  public Attribute() {
    name = new AttributeName();
    type = null;
    value = null;
  }

  public Attribute(AttributeName name, AttributeType type, Type value) {
    Preconditions.checkNotNull(name, "Attribute name cannot be null");
    Preconditions.checkNotNull(type, "Attribute type cannot be null");
    this.name = name;
    this.type = type;
    this.value = value;
  }

  public AttributeName getName() {
    return name;
  }

  public AttributeType getType() {
    return type;
  }

  public Type getValue() {
    return value;
  }

  @Override
  @SuppressWarnings("unchecked")
  public void readFields(ObjectInput in) throws IOException, ClassNotFoundException {
    name.readFields(in);
    type = AttributeType.readInstance(in);
    // TODO(stupaq): how not to rely on Java's serialization here
    value = (Type) in.readObject();
  }

  @Override
  public void writeFields(ObjectOutput out) throws IOException {
    name.writeFields(out);
    AttributeType.writeInstance(out, type);
    // TODO(stupaq): how not to rely on Java's serialization here
    out.writeObject(value);
  }

  @Override
  public boolean equals(Object o) {
    Attribute attribute = (Attribute) o;
    return this == o || !(o == null || getClass() != o.getClass()) && name.equals(attribute.name)
                        && type == attribute.type && !(value != null ? !value
        .equals(attribute.value) : attribute.value != null);

  }

  @Override
  public int hashCode() {
    int result = name.hashCode();
    result = 31 * result + (type != null ? type.hashCode() : 0);
    result = 31 * result + (value != null ? value.hashCode() : 0);
    return result;
  }
}
