package stupaq.cloudatlas.interpreter;

import org.junit.Test;

import stupaq.cloudatlas.attribute.types.CAQuery;
import stupaq.cloudatlas.zone.ZoneManagementInfo;
import stupaq.cloudatlas.zone.hierarchy.ZoneHierarchy;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static stupaq.cloudatlas.attribute.types.AttributeValueTestUtils.Doub;
import static stupaq.cloudatlas.attribute.types.AttributeValueTestUtils.Int;
import static stupaq.cloudatlas.zone.hierarchy.ZoneHierarchyTestUtils.Attr;
import static stupaq.cloudatlas.zone.hierarchy.ZoneHierarchyTestUtils.Name;
import static stupaq.cloudatlas.zone.hierarchy.ZoneHierarchyTestUtils.oneLevelHierarchy;

public class SingleQueryUpdaterTest {
  @Test
  public void testSimple1() throws Exception {
    ZoneHierarchy<ZoneManagementInfo> root =
        oneLevelHierarchy(Attr("attr", Int(1)), Attr("attr", Int(4)), Attr("attr", Int(6)),
            Attr("attr", Int(7)), Attr("attr", Int(0)));
    ZoneManagementInfo rootZmi = root.getPayload();
    assertEquals(Attr("attr", Int(1)), rootZmi.getAttribute(Name("attr")).get());
    assertFalse(rootZmi.getAttribute(Name("attr_")).isPresent());
    CAQuery query = new CAQuery("SELECT to_integer(avg(attr)) AS attr, avg(attr) AS attr_");
    root.zipFromLeaves(new SingleQueryUpdater(query));
    assertEquals(Attr("attr", Int(4)), rootZmi.getAttribute(Name("attr")).get());
    assertEquals(Attr("attr_", Doub(4.25)), rootZmi.getAttribute(Name("attr_")).get());
  }
}
