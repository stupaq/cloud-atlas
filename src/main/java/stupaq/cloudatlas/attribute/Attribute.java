package stupaq.cloudatlas.attribute;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

import stupaq.cloudatlas.naming.AttributeName;
import stupaq.compact.CompactSerializable;
import stupaq.compact.CompactSerializer;
import stupaq.compact.SerializableImplementation;
import stupaq.compact.TypeDescriptor;
import stupaq.compact.TypeRegistry;

import static stupaq.compact.CompactSerializers.Collection;

@Immutable
public final class Attribute<Type extends AttributeValue>
    implements CompactSerializable, Serializable {
  public static final CompactSerializer<Attribute> SERIALIZER = new CompactSerializer<Attribute>() {
    @Override
    public Attribute readInstance(ObjectInput in) throws IOException {
      return new Attribute<>(AttributeName.SERIALIZER.readInstance(in),
          (AttributeValue) TypeRegistry.readObject(in));
    }

    @Override
    public void writeInstance(ObjectOutput out, Attribute object) throws IOException {
      AttributeName.SERIALIZER.writeInstance(out, object.name);
      TypeRegistry.writeObject(out, object.value);
    }
  };
  public static final CompactSerializer<Map<AttributeName, Attribute>> MAP_SERIALIZER =
      new CompactSerializer<Map<AttributeName, Attribute>>() {
        @Override
        public Map<AttributeName, Attribute> readInstance(ObjectInput in) throws IOException {
          HashMap<AttributeName, Attribute> map = Maps.newHashMap();
          for (Attribute attribute : Collection(SERIALIZER).readInstance(in)) {
            map.put(attribute.name(), attribute);
          }
          return map;
        }

        @Override
        public void writeInstance(ObjectOutput out, Map<AttributeName, Attribute> map)
            throws IOException {
          Collection(SERIALIZER).writeInstance(out, map.values());
        }
      };
  private static final long serialVersionUID = 1L;
  @Nonnull private final AttributeName name;
  @Nonnull private transient Type value;

  public Attribute(@Nonnull AttributeName name, @Nonnull Type value) {
    Preconditions.checkNotNull(name, "AttributeName cannot be null");
    Preconditions.checkNotNull(value, "AttributeValue cannot be null");
    this.name = name;
    this.value = value;
  }

  @SerializableImplementation
  private void writeObject(ObjectOutputStream out) throws IOException {
    out.defaultWriteObject();
    TypeRegistry.writeObject(out, value);
  }

  @SerializableImplementation
  private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
    in.defaultReadObject();
    value = TypeRegistry.readObject(in);
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
