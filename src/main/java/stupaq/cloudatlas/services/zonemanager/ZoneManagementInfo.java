package stupaq.cloudatlas.services.zonemanager;

import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Maps;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import stupaq.cloudatlas.attribute.Attribute;
import stupaq.cloudatlas.attribute.AttributeName;
import stupaq.cloudatlas.naming.LocalName;
import stupaq.cloudatlas.services.zonemanager.hierarchy.ZoneHierarchy.Hierarchical;
import stupaq.compact.CompactSerializable;
import stupaq.compact.CompactSerializer;
import stupaq.compact.TypeDescriptor;

public final class ZoneManagementInfo implements CompactSerializable, Hierarchical {
  public static final CompactSerializer<ZoneManagementInfo> SERIALIZER =
      new CompactSerializer<ZoneManagementInfo>() {
        @Override
        public ZoneManagementInfo readInstance(ObjectInput in) throws IOException {
          return new ZoneManagementInfo(LocalName.SERIALIZER.readInstance(in),
              Attribute.MAP_SERIALIZER.readInstance(in));
        }

        @Override
        public void writeInstance(ObjectOutput out, ZoneManagementInfo object) throws IOException {
          LocalName.SERIALIZER.writeInstance(out, object.localName);
          Attribute.MAP_SERIALIZER.writeInstance(out, object.attributes);
        }
      };
  private final LocalName localName;
  private final Map<AttributeName, Attribute> attributes;

  public ZoneManagementInfo(LocalName localName) {
    this(localName, Maps.<AttributeName, Attribute>newHashMap());
  }

  public ZoneManagementInfo(LocalName localName, Map<AttributeName, Attribute> attributes) {
    this.localName = localName;
    this.attributes = attributes;
  }

  @Override
  public LocalName localName() {
    return localName;
  }

  public void recomputedAttribute(Attribute attribute) {
    attributes.put(attribute.getName(), attribute);
  }

  public void removeAttribute(AttributeName name) {
    attributes.remove(name);
  }

  public Optional<Attribute> getAttribute(AttributeName name) {
    return Optional.fromNullable(attributes.get(name));
  }

  public Collection<Attribute> getPublicAttributes() {
    return FluentIterable.from(attributes.values()).filter(new Predicate<Attribute>() {
      @Override
      public boolean apply(Attribute attribute) {
        return !attribute.getName().isSpecial();
      }
    }).toList();
  }

  public Collection<Attribute> getPrivateAttributes() {
    return FluentIterable.from(attributes.values()).filter(new Predicate<Attribute>() {
      @Override
      public boolean apply(Attribute attribute) {
        return attribute.getName().isSpecial();
      }
    }).toList();
  }

  public ZoneManagementInfo export() {
    return new ZoneManagementInfo(localName, new HashMap<>(attributes));
  }

  @Override
  public boolean equals(Object o) {
    ZoneManagementInfo that = (ZoneManagementInfo) o;
    return this == o ||
        !(o == null || getClass() != o.getClass()) && attributes.equals(that.attributes) &&
            localName.equals(that.localName);
  }

  @Override
  public int hashCode() {
    int result = localName.hashCode();
    result = 31 * result + attributes.hashCode();
    return result;
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    boolean skip = true;
    for (Attribute attribute : attributes.values()) {
      builder.append(skip ? "" : "\n").append(attribute.toString());
      skip = false;
    }
    return builder.toString();
  }

  @Override
  public TypeDescriptor descriptor() {
    return TypeDescriptor.ZoneManagementInfo;
  }
}
