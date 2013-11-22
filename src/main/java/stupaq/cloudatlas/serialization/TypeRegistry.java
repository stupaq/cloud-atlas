package stupaq.cloudatlas.serialization;

import com.google.common.base.Preconditions;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import stupaq.cloudatlas.attribute.values.CABoolean;
import stupaq.cloudatlas.attribute.values.CAContact;
import stupaq.cloudatlas.attribute.values.CADouble;
import stupaq.cloudatlas.attribute.values.CADuration;
import stupaq.cloudatlas.attribute.values.CAInteger;
import stupaq.cloudatlas.attribute.values.CAList;
import stupaq.cloudatlas.attribute.values.CASet;
import stupaq.cloudatlas.attribute.values.CAString;
import stupaq.cloudatlas.attribute.values.CATime;

public final class TypeRegistry {
  private static final Map<TypeID, TypeFactory> idToFactory = new EnumMap<>(TypeID.class);
  private static final Map<Class<?>, TypeID> classToID = new HashMap<>();
  private static final AtomicBoolean registerDefaultGuard = new AtomicBoolean(false);

  private TypeRegistry() {
  }

  public static <Type> void registerType(final Class<Type> clazz, TypeID id) {
    registerType(clazz, id, new TypeFactory<Type>() {
      @Override
      public Type newInstance() {
        try {
          return clazz.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
          throw new RuntimeException(e);
        }
      }
    });
  }

  public static <Type> void registerType(Class<Type> clazz, TypeID id, TypeFactory<Type> factory) {
    Preconditions.checkNotNull(clazz, "Class cannot be null");
    Preconditions.checkNotNull(id, "TypeID cannot be null");
    Preconditions.checkNotNull(factory, "TypeFactory cannot be null");
    TypeFactory oldFactory = idToFactory.get(id);
    Preconditions.checkState(oldFactory == null || oldFactory.equals(factory),
        "Cannot register different factory for TypeID: " + id);
    idToFactory.put(id, factory);
    classToID.put(clazz, id);
  }

  public static TypeID resolveType(Class clazz) {
    TypeID id = classToID.get(clazz);
    Preconditions
        .checkArgument(id != null, "TypeID for: " + clazz.getSimpleName() + " was not registered");
    return id;
  }

  @SuppressWarnings("unchecked")
  public static <T> T newInstance(TypeID id) {
    TypeFactory factory = idToFactory.get(id);
    Preconditions.checkNotNull(factory, "TypeFactory for TypeID: " + id + " not found");
    return (T) factory.newInstance();
  }

  public static <T extends CompactSerializable> T readObject(ObjectInput in)
      throws IOException, ClassNotFoundException {
    TypeID id = TypeID.readInstance(in);
    T instance = newInstance(id);
    instance.readFields(in);
    return instance;
  }

  public static <T extends CompactSerializable> void writeObject(ObjectOutput out, T object)
      throws IOException {
    TypeID id = resolveType(object.getClass());
    TypeID.writeInstance(out, id);
    object.writeFields(out);
  }

  public static void registerDefaultTypes() {
    if (registerDefaultGuard.getAndSet(true)) {
      return;
    }

    registerType(CABoolean.class, TypeID.ATTRIBUTE_TYPES_BOOLEAN);
    registerType(CAContact.class, TypeID.ATTRIBUTE_TYPES_CONTACT);
    registerType(CADouble.class, TypeID.ATTRIBUTE_TYPES_DOUBLE);
    registerType(CADuration.class, TypeID.ATTRIBUTE_TYPES_DURATION);
    registerType(CAInteger.class, TypeID.ATTRIBUTE_TYPES_INTEGER);
    registerType(CAList.class, TypeID.ATTRIBUTE_TYPES_LIST);
    registerType(CASet.class, TypeID.ATTRIBUTE_TYPES_SET);
    registerType(CAString.class, TypeID.ATTRIBUTE_TYPES_STRING);
    registerType(CATime.class, TypeID.ATTRIBUTE_TYPES_TIME);
  }

  public static interface TypeFactory<T> {
    public T newInstance();
  }
}
