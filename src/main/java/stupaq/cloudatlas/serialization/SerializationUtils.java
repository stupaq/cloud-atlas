package stupaq.cloudatlas.serialization;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public final class SerializationUtils {
  private SerializationUtils() {
  }

  @SuppressWarnings("unchecked")
  public static <T extends CompactSerializable> T clone(T object) throws Exception {
    return deserialize(serialize(object), (Class<T>) object.getClass());
  }

  public static <T extends CompactSerializable> byte[] serialize(T object) throws Exception {
    try (
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos)
    ) {
      object.writeFields(oos);
      oos.flush();
      return baos.toByteArray();
    }
  }

  public static <T extends CompactSerializable> T deserialize(byte[] objectBytes, Class<T> clazz)
      throws Exception {
    try (ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(objectBytes))) {
      T res = clazz.newInstance();
      res.readFields(ois);
      return res;
    }
  }

}
