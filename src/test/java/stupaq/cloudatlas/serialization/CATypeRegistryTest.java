package stupaq.cloudatlas.serialization;

import org.junit.Test;

import java.io.IOException;

import stupaq.cloudatlas.attribute.Attribute;
import stupaq.cloudatlas.attribute.values.CATime;
import stupaq.cloudatlas.naming.AttributeName;
import stupaq.cloudatlas.naming.LocalName;
import stupaq.cloudatlas.services.zonemanager.ZoneManagementInfo;
import stupaq.compact.CompactSerializableTestUtils;

import static org.junit.Assert.assertEquals;

public class CATypeRegistryTest {
  static {
    CATypeRegistry.registerCATypes();
  }

  @SuppressWarnings("unchecked")
  @Test
  public void testObject0() throws IOException {
    ZoneManagementInfo zmi = new ZoneManagementInfo(LocalName.getNotRoot("warsaw"));
    zmi.setPrime(new Attribute(AttributeName.valueOf("timestamp"), CATime.now()));
    assertEquals(zmi, CompactSerializableTestUtils.clone(zmi));
  }
}
