package stupaq.compact;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public final class CompactSerializableTestUtils {
  private CompactSerializableTestUtils() {
  }

  public static <Type extends CompactSerializable> Type clone(Type object) throws IOException {
    return deserialize(serialize(object));
  }

  public static <Type extends CompactSerializable> byte[] serialize(Type object)
      throws IOException {
    try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
         CompactOutput oos = new CompactOutput(baos)) {
      TypeRegistry.writeObject(oos, object);
      oos.flush();
      System.err.println("Serialized object of class: " + object.getClass().getSimpleName() +
          " size on the wire: " + baos.size());
      return baos.toByteArray();
    }
  }

  public static <Type extends CompactSerializable> Type deserialize(byte[] objectBytes)
      throws IOException {
    try (CompactInput ois = new CompactInput(new ByteArrayInputStream(objectBytes))) {
      return TypeRegistry.readObject(ois);
    }
  }

}
