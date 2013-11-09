package stupaq.cloudatlas.attribute;

import com.google.common.base.Preconditions;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import stupaq.cloudatlas.serialization.CompactSerializable;
import stupaq.cloudatlas.serialization.SerializationOnly;
import stupaq.cloudatlas.serialization.TypeRegistry;

public final class Attribute<Type extends AttributeValue> implements CompactSerializable {
  private final AttributeName name;
  private Type value;

  @SerializationOnly
  public Attribute() {
    name = new AttributeName();
    value = null;
  }

  public Attribute(AttributeName name, Type value) {
    Preconditions.checkNotNull(name, "AttributeName cannot be null");
    this.name = name;
    this.value = value;
  }

  public AttributeName getName() {
    return name;
  }

  public Type getValue() {
    return value;
  }

  @Override
  @SuppressWarnings("unchecked")
  public void readFields(ObjectInput in) throws IOException, ClassNotFoundException {
    name.readFields(in);
    value = TypeRegistry.readObject(in);
  }

  @Override
  public void writeFields(ObjectOutput out) throws IOException {
    name.writeFields(out);
    TypeRegistry.writeObject(out, value);
  }

  @Override
  public int hashCode() {
    int result = name.hashCode();
    result = 31 * result + (value != null ? value.hashCode() : 0);
    return result;
  }

  @Override
  public boolean equals(Object o) {
    Attribute attribute = (Attribute) o;
    return this == o || !(o == null || getClass() != o.getClass()) && name.equals(attribute.name)
                        && !(value != null ? !value.equals(attribute.value)
                                           : attribute.value != null);
  }
}
