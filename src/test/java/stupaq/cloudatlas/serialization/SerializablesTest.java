package stupaq.cloudatlas.serialization;

import org.apache.commons.lang.SerializationUtils;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.Serializable;
import java.util.Arrays;

import stupaq.cloudatlas.attribute.Attribute;
import stupaq.cloudatlas.attribute.values.CABoolean;
import stupaq.cloudatlas.attribute.values.CAContact;
import stupaq.cloudatlas.attribute.values.CAList;
import stupaq.cloudatlas.naming.AttributeName;
import stupaq.cloudatlas.naming.EntityName;
import stupaq.cloudatlas.naming.GlobalName;
import stupaq.cloudatlas.naming.LocalName;
import stupaq.cloudatlas.query.typecheck.TypeInfo;
import stupaq.cloudatlas.services.zonemanager.ZoneManagementInfo;
import stupaq.cloudatlas.services.zonemanager.hierarchy.ZoneHierarchy;

import static org.junit.Assert.assertEquals;

public class SerializablesTest {
  @BeforeClass
  public static void setUpClass() {
    CATypeRegistry.registerCATypes();
  }

  @Test
  public void testSerializables() {
    GlobalName globalName = GlobalName.parse("/warsaw/home");
    AttributeName attributeName = AttributeName.valueOf("something");
    Attribute<CAList<CABoolean>> listAttribute = new Attribute<>(attributeName,
        new CAList<>(TypeInfo.of(CABoolean.class), Arrays.asList(new CABoolean(true))));
    CAContact contact = new CAContact("Some contact");
    ZoneManagementInfo zmi = new ZoneManagementInfo(LocalName.getNotRoot("warsaw"));
    zmi.setPrime(listAttribute);
    EntityName entity = new EntityName(globalName, attributeName);
    LocalName localName = LocalName.getNotRoot("child");
    ZoneHierarchy<LocalName> hierarchy = new ZoneHierarchy<>(LocalName.getRoot());
    new ZoneHierarchy<>(localName).attachTo(hierarchy);
    // Try to serialize
    assertSerializable(globalName);
    assertSerializable(listAttribute);
    assertSerializable(contact);
    assertSerializable(zmi);
    assertSerializable(entity);
    assertSerializable(localName);
    assertSerializable(hierarchy);
  }

  private void assertSerializable(Serializable object) {
    Object clone = SerializationUtils.clone(object);
    assertEquals(object, clone);
  }
}
