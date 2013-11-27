package stupaq.compact;

import com.google.common.base.Preconditions;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.EnumMap;
import java.util.Map;

import javax.annotation.Nonnull;

public class TypeRegistry {
  private TypeRegistry() {
  }

  private static final Map<TypeDescriptor, CompactSerializer> serializers =
      new EnumMap<>(TypeDescriptor.class);

  @SuppressWarnings("unchecked")
  public static <Type> CompactSerializer<Type> resolve(@Nonnull TypeDescriptor descriptor)
      throws TypeRegistryException {
    Preconditions.checkNotNull(descriptor);
    CompactSerializer<Type> serializer = serializers.get(descriptor);
    if (serializer == null) {
      throw new TypeRegistryException("Cannot resolve serializer for descriptor: " + descriptor);
    }
    return serializer;
  }

  public static <Type> CompactSerializer<Type> resolveOrThrow(TypeDescriptor descriptor)
      throws IOException {
    try {
      return resolve(descriptor);
    } catch (TypeRegistryException e) {
      throw new IOException("Resolver failure", e);
    }
  }

  public static <Type> void register(@Nonnull TypeDescriptor descriptor,
      @Nonnull CompactSerializer<Type> serializer) throws TypeRegistryException {
    Preconditions.checkNotNull(descriptor);
    Preconditions.checkNotNull(serializer);
    if (serializers.containsKey(descriptor)) {
      throw new TypeRegistryException(
          "Cannot re-register serializer for descriptor: " + descriptor);
    }
    serializers.put(descriptor, serializer);
  }

  public static <Type> Type readObject(ObjectInput in) throws IOException {
    return TypeRegistry.<Type>resolveOrThrow(TypeDescriptor.readInstance(in)).readInstance(in);
  }

  public static <Type extends CompactSerializable> void writeObject(ObjectOutput out, Type object)
      throws IOException {
    TypeDescriptor.writeInstance(out, object.descriptor());
    TypeRegistry.<Type>resolveOrThrow(object.descriptor()).writeInstance(out, object);
  }

  public static class TypeRegistryException extends IllegalStateException {
    public TypeRegistryException(String message) {
      super(message);
    }
  }
}
