package stupaq.cloudatlas.zone;

import stupaq.cloudatlas.attribute.Attribute;
import stupaq.cloudatlas.attribute.AttributeName;
import stupaq.cloudatlas.attribute.AttributeValue;
import stupaq.cloudatlas.attribute.types.CAContact;
import stupaq.cloudatlas.attribute.types.CASet;
import stupaq.cloudatlas.naming.GlobalName;
import stupaq.cloudatlas.naming.LocalName;
import stupaq.cloudatlas.zone.hierarchy.ZoneHierarchy;

import static stupaq.cloudatlas.attribute.types.AttributeTypeTestUtils.Int;
import static stupaq.cloudatlas.attribute.types.AttributeTypeTestUtils.Str;

public final class ZoneHierarchyTestUtils {
  private ZoneHierarchyTestUtils() {
  }

  public static ZoneManagementInfo addAttrs(ZoneManagementInfo zmi, Attribute... attributes) {
    for (Attribute attribute : attributes) {
      zmi.addAttribute(attribute);
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

  private static LocalName Local(String nameStr) {
    return LocalName.valueOf(nameStr);
  }

  public static <Type extends AttributeValue> Attribute<Type> Attr(String name, Type value) {
    return new Attribute<>(AttributeName.valueOf(name), value);
  }
}
