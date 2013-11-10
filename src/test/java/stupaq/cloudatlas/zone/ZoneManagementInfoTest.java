package stupaq.cloudatlas.zone;

import org.junit.BeforeClass;
import org.junit.Test;

import stupaq.cloudatlas.attribute.Attribute;
import stupaq.cloudatlas.attribute.AttributeName;
import stupaq.cloudatlas.attribute.AttributeValue;
import stupaq.cloudatlas.attribute.types.CABoolean;
import stupaq.cloudatlas.attribute.types.CAContact;
import stupaq.cloudatlas.attribute.types.CADouble;
import stupaq.cloudatlas.attribute.types.CADuration;
import stupaq.cloudatlas.attribute.types.CAInteger;
import stupaq.cloudatlas.attribute.types.CAList;
import stupaq.cloudatlas.attribute.types.CASet;
import stupaq.cloudatlas.attribute.types.CAString;
import stupaq.cloudatlas.attribute.types.CATime;
import stupaq.cloudatlas.serialization.SerializationUtils;
import stupaq.cloudatlas.serialization.TypeRegistry;

import static org.junit.Assert.assertEquals;

public class ZoneManagementInfoTest {

  @BeforeClass
  public static void setUpClass() {
    TypeRegistry.registerDefaultTypes();
  }

  private static void addAttr(ZoneManagementInfo zmi, String name, AttributeValue value) {
    zmi.addAttribute(new Attribute<>(new AttributeName(name), value));
  }

  @Test
  public void testSerialization() throws Exception {
    ZoneManagementInfo zmi = new ZoneManagementInfo();
    addAttr(zmi, "attributeBoolean", new CABoolean(Boolean.TRUE));
    addAttr(zmi, "attributeContact", new CAContact("UW1"));
    addAttr(zmi, "attributeDouble", new CADouble(1.337D));
    addAttr(zmi, "attributeDuration", new CADuration(1337331L));
    addAttr(zmi, "attributeInteger", new CAInteger(1337L));
    addAttr(zmi, "attributeList", new CAList<>(new CAString("some..."), new CAString("string...")));
    addAttr(zmi, "attributeSet", new CASet<>(new CAInteger(13L), new CAInteger(37L)));
    addAttr(zmi, "attributeString", new CAString("aString"));
    addAttr(zmi, "attributeTime", new CATime(11733L));
    assertEquals(zmi, SerializationUtils.clone(zmi));
  }
}
