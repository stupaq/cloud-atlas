package stupaq.cloudatlas;

import org.junit.BeforeClass;
import org.junit.Test;

import stupaq.cloudatlas.attribute.Attribute;
import stupaq.cloudatlas.attribute.AttributeName;
import stupaq.cloudatlas.attribute.AttributeType;
import stupaq.cloudatlas.attribute.types.CABoolean;
import stupaq.cloudatlas.attribute.types.CAInteger;
import stupaq.cloudatlas.attribute.types.CAString;
import stupaq.cloudatlas.serialization.SerializationUtils;
import stupaq.cloudatlas.serialization.TypeRegistry;
import stupaq.cloudatlas.zone.ZoneManagementInfo;

import static org.junit.Assert.assertEquals;

public class ZoneManagementInfoTest {

  @BeforeClass
  public static void setUpClass() {
    TypeRegistry.registerDefaultTypes();
  }

  @Test
  public void testSerialization() throws Exception {
    ZoneManagementInfo zmi = new ZoneManagementInfo();
    zmi.addAttribute(new Attribute<>(new AttributeName("attributeName1"), AttributeType.BOOLEAN,
                                     new CABoolean(Boolean.TRUE)));
    zmi.addAttribute(new Attribute<>(new AttributeName("attributeName2"), AttributeType.INTEGER,
                                     new CAInteger(1337L)));
    zmi.addAttribute(new Attribute<>(new AttributeName("attributeName3"), AttributeType.STRING,
                                     new CAString("aString")));
    ZoneManagementInfo zmiClone = SerializationUtils.clone(zmi);
    assertEquals(zmi, zmiClone);
  }
}
