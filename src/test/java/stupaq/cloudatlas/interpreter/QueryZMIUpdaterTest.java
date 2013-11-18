package stupaq.cloudatlas.interpreter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;

import java.util.Collections;

import stupaq.cloudatlas.attribute.types.CAQuery;
import stupaq.cloudatlas.zone.ZoneManagementInfo;
import stupaq.cloudatlas.zone.hierarchy.ZoneHierarchy;

import static org.junit.Assert.assertEquals;
import static stupaq.cloudatlas.attribute.types.AttributeTypeTestUtils.Doub;
import static stupaq.cloudatlas.attribute.types.AttributeTypeTestUtils.Int;
import static stupaq.cloudatlas.zone.hierarchy.ZoneHierarchyTestUtils.Attr;
import static stupaq.cloudatlas.zone.hierarchy.ZoneHierarchyTestUtils.oneLevelHierarchy;

public class QueryZMIUpdaterTest {
  private static final Log LOG = LogFactory.getLog(QueryZMIUpdater.class);

  @Test
  public void testZMIUpdater() throws Exception {
    ZoneHierarchy<ZoneManagementInfo> hierarchy =
        oneLevelHierarchy(Attr("attr", Int(1)), Attr("attr", Int(12)), Attr("attr", Int(4)),
            Attr("attr", Int(8)));
    LOG.info("\n" + hierarchy);
    CAQuery query = new CAQuery("SELECT avg(attr)");
    hierarchy.aggregate(new QueryZMIUpdater(query));
    assertEquals(Collections.singleton(Attr("attr", Doub(3.0))),
        hierarchy.getPayload().getAttributes());
  }
}
