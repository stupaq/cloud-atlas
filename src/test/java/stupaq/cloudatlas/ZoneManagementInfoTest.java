package stupaq.cloudatlas;

import org.junit.Test;

import stupaq.cloudatlas.attribute.Attribute;
import stupaq.cloudatlas.attribute.AttributeName;
import stupaq.cloudatlas.attribute.AttributeType;
import stupaq.cloudatlas.serialization.SerializationUtils;

import static org.junit.Assert.assertEquals;

public class ZoneManagementInfoTest {

  @Test
  public void testSerialization() throws Exception {
    ZoneManagementInfo zmi = new ZoneManagementInfo();
    zmi.addAttribute(
        new Attribute<>(new AttributeName("attributeName1"), AttributeType.BOOLEAN, Boolean.TRUE));
    zmi.addAttribute(
        new Attribute<>(new AttributeName("attributeName2"), AttributeType.INTEGER, 1337L));
    zmi.addAttribute(
        new Attribute<>(new AttributeName("attributeName3"), AttributeType.STRING, "aString"));
    ZoneManagementInfo zmiClone = SerializationUtils.clone(zmi);
    assertEquals(zmi, zmiClone);
  }
}
