package stupaq.cloudatlas.module.zonemanager;

import org.junit.BeforeClass;

import stupaq.cloudatlas.attribute.Attribute;
import stupaq.cloudatlas.attribute.AttributeName;
import stupaq.cloudatlas.attribute.AttributeValue;
import stupaq.cloudatlas.naming.LocalName;
import stupaq.cloudatlas.serialization.SerializationUtils;
import stupaq.cloudatlas.serialization.TypeRegistry;

import static org.junit.Assert.assertEquals;
import static stupaq.cloudatlas.attribute.values.AttributeValueTestUtils.*;
import static stupaq.cloudatlas.attribute.types.TypeInfoTestUtils.TInt;
import static stupaq.cloudatlas.attribute.types.TypeInfoTestUtils.TStr;

public class ZoneManagementInfoTest {

  @BeforeClass
  public static void setUpClass() {
    TypeRegistry.registerDefaultTypes();
  }

  private static void addAttr(ZoneManagementInfo zmi, String name, AttributeValue value) {
    zmi.updateAttribute(new Attribute<>(AttributeName.valueOf(name), value));
  }

  // FIXME @Test
  public void testSerialization() throws Exception {
    ZoneManagementInfo zmi = new ZoneManagementInfo(LocalName.getRoot());
    addAttr(zmi, "attributeBoolean", Bool(true));
    addAttr(zmi, "attributeContact", Cont("UW1"));
    addAttr(zmi, "attributeDouble", Doub(1.337D));
    addAttr(zmi, "attributeDuration", Dur(1337331L));
    addAttr(zmi, "attributeInteger", Int(1337L));
    addAttr(zmi, "attributeList", List(TStr(), Str("some..."), Str("string...")));
    addAttr(zmi, "attributeSet", Set(TInt(), Int(13L), Int(37L)));
    addAttr(zmi, "attributeString", Str("aString"));
    addAttr(zmi, "attributeTime", Time(11733L));
    assertEquals(zmi, SerializationUtils.clone(zmi));
  }
}
