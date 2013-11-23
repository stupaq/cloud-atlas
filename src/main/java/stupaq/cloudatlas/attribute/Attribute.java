package stupaq.cloudatlas.attribute;

import com.google.common.base.Preconditions;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

import stupaq.compact.CompactSerializable;
import stupaq.compact.CompactSerializer;
import stupaq.compact.TypeDescriptor;
import stupaq.compact.TypeRegistry;

@Immutable
public final class Attribute<Type extends AttributeValue> implements CompactSerializable {
  public static final CompactSerializer<Attribute> SERIALIZER = new CompactSerializer<Attribute>() {
    @SuppressWarnings("unchecked")
    @Override
    public Attribute readInstance(ObjectInput in) throws IOException {
      return new Attribute(AttributeName.SERIALIZER.readInstance(in),
          (AttributeValue) TypeRegistry.readObject(in));
    }

    @Override
    public void writeInstance(ObjectOutput out, Attribute object) throws IOException {
      AttributeName.SERIALIZER.writeInstance(out, object.name);
      TypeRegistry.writeObject(out, object.value);
    }
  };
  @Nonnull private final AttributeName name;
  @Nonnull private final Type value;

  public Attribute(@Nonnull AttributeName name, @Nonnull Type value) {
    Preconditions.checkNotNull(name, "AttributeName cannot be null");
    Preconditions.checkNotNull(value, "AttributeValue cannot be null");
    this.name = name;
    this.value = value;
  }

  @Nonnull
  public AttributeName getName() {
    return name;
  }

  @Nonnull
  public Type getValue() {
    return value;
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

  @Override
  public TypeDescriptor descriptor() {
    return TypeDescriptor.Attribute;
  }
}
