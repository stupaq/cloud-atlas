package stupaq.cloudatlas.modules.queryexecutor;

import org.junit.Test;

import stupaq.cloudatlas.zone.ZoneManagementInfo;
import stupaq.cloudatlas.zone.hierarchy.ZoneHierarchy;

import static stupaq.cloudatlas.attribute.types.AttributeTypeTestUtils.*;
import static stupaq.cloudatlas.zone.ZoneHierarchyTestUtils.Attr;
import static stupaq.cloudatlas.zone.ZoneHierarchyTestUtils.Node;
import static stupaq.cloudatlas.zone.ZoneHierarchyTestUtils.Zmi;

public class QueryExecutorEngineTest {
  private static final String V7 = "/uw/violet07", K31 = "/uw/khaki31", K13 = "/uw/khaki13", W1 =
      "/pjwstk/whatever01", W2 = "/pjwstk/whatever02";

  @Test
  public void testSimple() {
    // Create zones
    ZoneHierarchy<ZoneManagementInfo> hierarchy =
        Node(Zmi("/", V7, "2012/11/09 20:10:17.342 CET", SetEmpty(Cont()), 0),
            Node(Zmi("/uw", V7, "2012/11/09 20:8:13.123 CET", SetEmpty(Cont()), 0), Node(
                Zmi("/uw/violet07", V7, "2012/11/09 18:00:00.000 CET",
                    Set(Cont("UW1A"), Cont("UW1B"), Cont("UW1C")), 1,
                    Attr("members", Set(Cont("UW1"))),
                    Attr("creation", Str("2011/11/09 20:8:13.123 CET").to().Time()),
                    Attr("cpu_usage", Doub(0.9)), Attr("num_cores", Int(3)),
                    Attr("has_ups", Bool()), Attr("some_names", List(Str("tola"), Str("tosia"))),
                    Attr("expiry", Str("+13 12:00:00.000").to().Duration()))), Node(
                Zmi("/uw/khaki31", K31, "2012/11/09 20:03:00.000", Set(Cont("UW2A")), 1,
                    Attr("members", Set(Cont("UW2A"))),
                    Attr("creation", Str("2011/11/09 20:12:13.123 CET").to().Time()),
                    Attr("cpu_usage", Doub()), Attr("num_cores", Int(3)),
                    Attr("has_ups", Bool(false)),
                    Attr("some_names", List(Str("agatka"), Str("beatka"), Str("celina"))),
                    Attr("expiry", Str("-13 11:00:00.000").to().Duration()))), Node(
                Zmi("/uw/khaki13", K13, "2012/11/09 21:03:00.000", Set(Cont("UW3A"), Cont("UW3B")),
                    1, Attr("members", Set(Cont("UW3A"))), Attr("creation", Time()),
                    Attr("cpu_usage", Doub(0.1)), Attr("num_cores", Int()),
                    Attr("has_ups", Bool(true)), Attr("some_names", ListEmpty(Str())),
                    Attr("expiry", Dur())))),
            Node(Zmi("/pjwstk", W1, "2012/11/09 20:8:13.123 CET", SetEmpty(Cont()), 0), Node(
                Zmi("/pjwstk/whatever01", W1, "2012/11/09 21:12:00.000", Set(Cont("PJ1")), 1,
                    Attr("members", Set(Cont("PJ1"))),
                    Attr("creation", Str("2012/10/18 07:03:00.000 CET").to().Time()),
                    Attr("cpu_usage", Doub(0.1)), Attr("num_cores", Int(7)),
                    Attr("php_modules", List(Str("rewrite"))))), Node(
                Zmi("/pjwstk/whatever02", W2, "2012/11/09 21:13:00.000", Set(Cont("PJ2")), 1,
                    Attr("members", Set(Cont("PJ2"))),
                    Attr("creation", Str("2012/10/18 07:04:00.000 CET").to().Time()),
                    Attr("cpu_usage", Doub(0.4)), Attr("num_cores", Int(13)),
                    Attr("php_modules", List(Str("odbc")))))));
  }
}
