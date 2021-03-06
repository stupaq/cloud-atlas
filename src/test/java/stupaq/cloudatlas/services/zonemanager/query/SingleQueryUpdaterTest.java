package stupaq.cloudatlas.services.zonemanager.query;

import org.junit.Test;

import stupaq.cloudatlas.attribute.values.CAQuery;
import stupaq.cloudatlas.services.zonemanager.ZoneManagementInfo;
import stupaq.cloudatlas.services.zonemanager.hierarchy.ZoneHierarchy;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static stupaq.cloudatlas.attribute.values.AttributeValueTestUtils.Doub;
import static stupaq.cloudatlas.attribute.values.AttributeValueTestUtils.Int;
import static stupaq.cloudatlas.services.zonemanager.hierarchy.ZoneHierarchyTestUtils.Attr;
import static stupaq.cloudatlas.services.zonemanager.hierarchy.ZoneHierarchyTestUtils.Name;
import static stupaq.cloudatlas.services.zonemanager.hierarchy.ZoneHierarchyTestUtils.oneLevelHierarchy;

public class SingleQueryUpdaterTest {
  @Test
  public void testSimple1() throws Exception {
    ZoneHierarchy<ZoneManagementInfo> root =
        oneLevelHierarchy(Attr("attr", Int(1)), Attr("attr", Int(4)), Attr("attr", Int(6)),
            Attr("attr", Int(7)), Attr("attr", Int(0)));
    ZoneManagementInfo rootZmi = root.payload();
    assertEquals(Attr("attr", Int(1)), rootZmi.get(Name("attr")).get());
    assertFalse(rootZmi.get(Name("attr_")).isPresent());
    CAQuery query = new CAQuery("SELECT to_integer(avg(attr)) AS attr, avg(attr) AS attr_");
    root.synthesizeFromLeaves(new SingleQueryUpdater(query));
    assertEquals(Attr("attr", Int(4)), rootZmi.get(Name("attr")).get());
    assertEquals(Attr("attr_", Doub(4.25)), rootZmi.get(Name("attr_")).get());
  }
}
