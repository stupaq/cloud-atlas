package stupaq.cloudatlas.interpreter;

import org.junit.Test;

import stupaq.cloudatlas.attribute.types.CAQuery;
import stupaq.cloudatlas.zone.ZoneManagementInfo;
import stupaq.cloudatlas.zone.hierarchy.ZoneHierarchy;

import static org.junit.Assert.assertEquals;
import static stupaq.cloudatlas.attribute.types.AttributeTypeTestUtils.Int;
import static stupaq.cloudatlas.zone.hierarchy.ZoneHierarchyTestUtils.Attr;
import static stupaq.cloudatlas.zone.hierarchy.ZoneHierarchyTestUtils.Name;
import static stupaq.cloudatlas.zone.hierarchy.ZoneHierarchyTestUtils.oneLevelHierarchy;

public class QueryZMIUpdaterTest {
  @Test
  public void testSimple1() throws Exception {
    ZoneHierarchy<ZoneManagementInfo> hierarchy =
        oneLevelHierarchy(Attr("attr", Int(1)), Attr("attr", Int(12)), Attr("attr", Int(5)),
            Attr("attr", Int(8)));
    assertEquals(Attr("attr", Int(1)), hierarchy.getPayload().getAttribute(Name("attr")).get());
    CAQuery query = new CAQuery("SELECT to_integer(avg(attr)) AS attr");
    hierarchy.aggregate(new QueryZMIUpdater(query));
    assertEquals(Attr("attr", Int(8)), hierarchy.getPayload().getAttribute(Name("attr")).get());
  }
}
