package stupaq.cloudatlas;

import org.junit.BeforeClass;
import org.junit.Test;

import stupaq.cloudatlas.attribute.Attribute;
import stupaq.cloudatlas.attribute.AttributeName;
import stupaq.cloudatlas.attribute.AttributeType;
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
import stupaq.cloudatlas.attribute.types.CATuple;
import stupaq.cloudatlas.serialization.SerializationUtils;
import stupaq.cloudatlas.serialization.TypeRegistry;
import stupaq.cloudatlas.zone.ZoneManagementInfo;

import static org.junit.Assert.assertEquals;

public class ZoneManagementInfoTest {

  @BeforeClass
  public static void setUpClass() {
    TypeRegistry.registerDefaultTypes();
  }

  private static void addAttr(ZoneManagementInfo zmi, String name, AttributeType type,
      AttributeValue value) {
    zmi.addAttribute(new Attribute<>(new AttributeName(name), type, value));
  }

  @Test
  public void testSerialization() throws Exception {
    ZoneManagementInfo zmi = new ZoneManagementInfo();
    addAttr(zmi, "attributeBoolean", AttributeType.BOOLEAN, new CABoolean(Boolean.TRUE));
    addAttr(zmi, "attributeContact", AttributeType.CONTACT, new CAContact("UW1"));
    addAttr(zmi, "attributeDouble", AttributeType.DOUBLE, new CADouble(1.337D));
    addAttr(zmi, "attributeDuration", AttributeType.DURATION, new CADuration(1337331L));
    addAttr(zmi, "attributeInteger", AttributeType.INTEGER, new CAInteger(1337L));
    addAttr(zmi, "attributeList", AttributeType.LIST,
        new CAList<>(new CAString("some..."), new CAString("string...")));
    addAttr(zmi, "attributeSet", AttributeType.SET,
        new CASet<>(new CAInteger(13L), new CAInteger(37L)));
    addAttr(zmi, "attributeString", AttributeType.STRING, new CAString("aString"));
    addAttr(zmi, "attributeTime", AttributeType.TIME, new CATime(11733L));
    addAttr(zmi, "attributeTuple", AttributeType.TUPLE,
        new CATuple(new CAString("some..."), new CATuple(new CAInteger(11L), new CABoolean(false)),
            new CAInteger(1337L)));
    ZoneManagementInfo zmiClone = SerializationUtils.clone(zmi);
    assertEquals(zmi, zmiClone);
  }
}
