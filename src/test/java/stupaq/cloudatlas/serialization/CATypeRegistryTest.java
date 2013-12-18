package stupaq.cloudatlas.serialization;

import org.junit.Test;

import java.io.IOException;
import java.util.Arrays;

import stupaq.cloudatlas.attribute.Attribute;
import stupaq.cloudatlas.attribute.AttributeName;
import stupaq.cloudatlas.attribute.values.CATime;
import stupaq.cloudatlas.messaging.messages.AttributesUpdateMessage;
import stupaq.cloudatlas.naming.GlobalName;
import stupaq.compact.CompactSerializable;
import stupaq.compact.CompactSerializableTestUtils;

import static org.junit.Assert.assertEquals;

public class CATypeRegistryTest {
  static {
    CATypeRegistry.registerCATypes();
  }

  @Test
  public void testObject0() throws IOException {
    CompactSerializable instance =
        new AttributesUpdateMessage(GlobalName.parse("/some/zone/at/some/level"),
            Arrays.asList(new Attribute(AttributeName.valueOf("timestamp"), CATime.now())), false);
    assertEquals(instance, CompactSerializableTestUtils.clone(instance));
  }
}
