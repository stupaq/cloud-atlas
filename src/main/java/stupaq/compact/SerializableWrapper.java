package stupaq.compact;

import com.google.common.base.Preconditions;

import java.io.IOException;
import java.io.Serializable;

public final class SerializableWrapper<Type extends CompactSerializable> implements Serializable {
  private static final long serialVersionUID = 1L;
  private transient Type object;

  protected SerializableWrapper(Type object) {
    this.object = object;
  }

  private void writeObject(CompactOutput out) throws IOException {
    Preconditions.checkNotNull(object);
    TypeRegistry.writeObject(out, object);
  }

  private void readObject(CompactInput in) throws IOException, ClassNotFoundException {
    object = TypeRegistry.readObject(in);
  }

  public Type get() {
    return object;
  }

  public static <Serializable extends CompactSerializable> SerializableWrapper<Serializable> wrap(
      Serializable object) {
    return new SerializableWrapper<>(object);
  }
}
