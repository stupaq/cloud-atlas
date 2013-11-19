package stupaq.cloudatlas.attribute;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import javax.annotation.Nonnull;

import stupaq.cloudatlas.attribute.types.CAString;
import stupaq.cloudatlas.serialization.CompactSerializable;
import stupaq.cloudatlas.serialization.SerializationOnly;
import stupaq.cloudatlas.serialization.TypeRegistry;
import stupaq.guava.base.PrimitiveWrapper;

public final class Attribute<Type extends AttributeValue> extends PrimitiveWrapper<Optional<Type>>
    implements CompactSerializable {
  @Nonnull
  private final AttributeName name;

  public Attribute(@Nonnull AttributeName name, Type value) {
    super(Optional.fromNullable(value));
    Preconditions.checkNotNull(name, "AttributeName cannot be null");
    this.name = name;
  }

  @SerializationOnly
  public Attribute() {
    super(Optional.<Type>absent());
    name = new AttributeName();
  }

  @Nonnull
  public AttributeName getName() {
    return name;
  }

  @Override
  @Nonnull
  public Optional<Type> get() {
    return super.get();
  }

  @Override
  @SuppressWarnings("unchecked")
  public void readFields(ObjectInput in) throws IOException, ClassNotFoundException {
    name.readFields(in);
    if (in.readBoolean()) {
      set(Optional.of((Type) TypeRegistry.readObject(in)));
    } else {
      set(Optional.<Type>absent());
    }
  }

  @Override
  public void writeFields(ObjectOutput out) throws IOException {
    name.writeFields(out);
    if (get().isPresent()) {
      out.writeBoolean(true);
      TypeRegistry.writeObject(out, get().get());
    } else {
      out.writeBoolean(false);
    }
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
    return name.equals(attribute.name) && get().equals(attribute.get());
  }

  @Override
  public int hashCode() {
    int result = name.hashCode();
    result = 31 * result + get().hashCode();
    return result;
  }

  @SuppressWarnings("unchecked")
  @Override
  public String toString() {
    return name + " = " + CAString.valueOf(get());
  }
}
