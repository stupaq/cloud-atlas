package stupaq.cloudatlas.modules.queryexecutor;

import org.junit.Test;

import stupaq.cloudatlas.zone.ZoneManagementInfo;
import stupaq.cloudatlas.zone.hierarchy.ZoneHierarchy;

import static stupaq.cloudatlas.attribute.types.AttributeTypeTestUtils.Cont;
import static stupaq.cloudatlas.attribute.types.AttributeTypeTestUtils.Set;
import static stupaq.cloudatlas.attribute.types.AttributeTypeTestUtils.SetEmpty;
import static stupaq.cloudatlas.zone.ZoneHierarchyTestUtils.Attr;
import static stupaq.cloudatlas.zone.ZoneHierarchyTestUtils.Node;
import static stupaq.cloudatlas.zone.ZoneHierarchyTestUtils.Zmi;

public class QueryExecutorEngineTest {
  private static final String V7 = "/uw/violet07", W1 = "/pjwstk/whatever";

  @Test
  public void testSimple() {
    // Create zones
    ZoneHierarchy<ZoneManagementInfo> hierarchy =
        Node(Zmi("/", V7, "2012/11/09 20:10:17.342 CET", SetEmpty(Cont()), 0),
            Node(Zmi("/uw", V7, "2012/11/09 20:8:13.123 CET", SetEmpty(Cont()), 0), Node(
                Zmi("/uw/violet07", V7, "2012/11/09 18:00:00.000 CET",
                    Set(Cont("UW1A"), Cont("UW1B"), Cont("UW1C")), 1, Attr("members", Set(Cont("UW1"))) /*TODO*/))),
            Node(Zmi("/pjwstk", W1, "2012/11/09 20:8:13.123 CET", SetEmpty(Cont()), 0)));
  }
}
