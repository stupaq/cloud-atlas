package stupaq.cloudatlas.zone.hierarchy;

import com.google.common.collect.FluentIterable;

import java.util.Arrays;

import stupaq.cloudatlas.attribute.Attribute;
import stupaq.cloudatlas.attribute.AttributeName;
import stupaq.cloudatlas.attribute.AttributeValue;
import stupaq.cloudatlas.attribute.types.CAContact;
import stupaq.cloudatlas.attribute.types.CASet;
import stupaq.cloudatlas.attribute.types.CATime;
import stupaq.cloudatlas.naming.GlobalName;
import stupaq.cloudatlas.zone.ZoneManagementInfo;
import stupaq.guava.base.Function1.CountingFunction1;

import static stupaq.cloudatlas.attribute.types.AttributeTypeTestUtils.*;

public final class ZoneHierarchyTestUtils {
  private static final String EP = CATime.epoch().to().String().toString();
  private static final String V7 = "/uw/violet07", K31 = "/uw/khaki31", K13 = "/uw/khaki13", W1 =
      "/pjwstk/whatever01", W2 = "/pjwstk/whatever02";

  private ZoneHierarchyTestUtils() {
  }

  @SuppressWarnings("unchecked")
  public static ZoneHierarchy<ZoneManagementInfo> oneLevelHierarchy(Attribute root,
      Attribute... children) {
    return Node(Zmi("/", V7, EP, SetEmpty(Cont()), 0, root),
        FluentIterable.from(Arrays.asList(children))
            .transform(new CountingFunction1<Attribute, ZoneHierarchy<ZoneManagementInfo>>() {
              @Override
              public ZoneHierarchy<ZoneManagementInfo> apply(int iteration, Attribute attribute) {
                return Node(Zmi("/uw" + iteration, K13, EP, SetEmpty(Cont()), 0, attribute));
              }
            }).toArray((Class) ZoneHierarchy.class));
  }

  public static ZoneHierarchy<ZoneManagementInfo> officialExampleHierarchy() {
    return Node(Zmi("/", V7, "2012/11/09 20:10:17.342 CET", SetEmpty(Cont()), 0),
        Node(Zmi("/uw", V7, "2012/11/09 20:8:13.123 CET", SetEmpty(Cont()), 0), Node(
            Zmi("/uw/violet07", V7, "2012/11/09 18:00:00.000 CET",
                Set(Cont("UW1A"), Cont("UW1B"), Cont("UW1C")), 1, Attr("members", Set(Cont("UW1"))),
                Attr("creation", Str("2011/11/09 20:8:13.123 CET").to().Time()),
                Attr("cpu_usage", Doub(0.9)), Attr("num_cores", Int(3)), Attr("has_ups", Bool()),
                Attr("some_names", List(Str("tola"), Str("tosia"))),
                Attr("expiry", Str("+13 12:00:00.000 CET").to().Duration()))), Node(
            Zmi("/uw/khaki31", K31, "2012/11/09 20:03:00.000 CET", Set(Cont("UW2A")), 1,
                Attr("members", Set(Cont("UW2A"))),
                Attr("creation", Str("2011/11/09 20:12:13.123 CET").to().Time()),
                Attr("cpu_usage", Doub()), Attr("num_cores", Int(3)), Attr("has_ups", Bool(false)),
                Attr("some_names", List(Str("agatka"), Str("beatka"), Str("celina"))),
                Attr("expiry", Str("-13 11:00:00.000 CET").to().Duration()))), Node(
            Zmi("/uw/khaki13", K13, "2012/11/09 21:03:00.000 CET", Set(Cont("UW3A"), Cont("UW3B")),
                1, Attr("members", Set(Cont("UW3A"))), Attr("creation", Time()),
                Attr("cpu_usage", Doub(0.1)), Attr("num_cores", Int()), Attr("has_ups", Bool(true)),
                Attr("some_names", ListEmpty(Str())), Attr("expiry", Dur())))),
        Node(Zmi("/pjwstk", W1, "2012/11/09 20:8:13.123 CET", SetEmpty(Cont()), 0), Node(
            Zmi("/pjwstk/whatever01", W1, "2012/11/09 21:12:00.000 CET", Set(Cont("PJ1")), 1,
                Attr("members", Set(Cont("PJ1"))),
                Attr("creation", Str("2012/10/18 07:03:00.000 CET").to().Time()),
                Attr("cpu_usage", Doub(0.1)), Attr("num_cores", Int(7)),
                Attr("php_modules", List(Str("rewrite"))))), Node(
            Zmi("/pjwstk/whatever02", W2, "2012/11/09 21:13:00.000 CET", Set(Cont("PJ2")), 1,
                Attr("members", Set(Cont("PJ2"))),
                Attr("creation", Str("2012/10/18 07:04:00.000 CET").to().Time()),
                Attr("cpu_usage", Doub(0.4)), Attr("num_cores", Int(13)),
                Attr("php_modules", List(Str("odbc")))))));
  }

  public static ZoneManagementInfo addAttrs(ZoneManagementInfo zmi, Attribute... attributes) {
    for (Attribute attribute : attributes) {
      zmi.updateAttribute(attribute);
    }
    return zmi;
  }

  @SafeVarargs
  public static ZoneHierarchy<ZoneManagementInfo> Node(ZoneManagementInfo root,
      ZoneHierarchy<ZoneManagementInfo>... children) {
    ZoneHierarchy<ZoneManagementInfo> rootNode = new ZoneHierarchy<>(root);
    for (ZoneHierarchy<ZoneManagementInfo> child : children) {
      child.rootAt(rootNode);
    }
    return rootNode;
  }

  public static ZoneManagementInfo Zmi(String fullName, String owner, String timestamp,
      CASet<CAContact> contacts, long cardinality, Attribute... attributes) {
    GlobalName globalName = GlobalName.parse(fullName);
    ZoneManagementInfo zmi = new ZoneManagementInfo(globalName.leaf());
    addAttrs(zmi, Attr("level", Int(globalName.leafLevel())), Attr("name", Str(fullName)),
        Attr("owner", Str(owner)), Attr("timestamp", Str(timestamp).to().Time()),
        Attr("contacts", contacts), Attr("cardinality", Int(cardinality)));
    addAttrs(zmi, attributes);
    return zmi;
  }

  public static <Type extends AttributeValue> Attribute<Type> Attr(String name, Type value) {
    return new Attribute<>(AttributeName.valueOf(name), value);
  }
}
