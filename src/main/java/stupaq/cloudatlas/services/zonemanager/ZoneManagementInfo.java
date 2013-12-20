package stupaq.cloudatlas.services.zonemanager;

import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Maps;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import stupaq.cloudatlas.attribute.Attribute;
import stupaq.cloudatlas.naming.AttributeName;
import stupaq.cloudatlas.naming.LocalName;
import stupaq.cloudatlas.services.zonemanager.hierarchy.ZoneHierarchy.Hierarchical;
import stupaq.compact.CompactSerializable;
import stupaq.compact.CompactSerializer;
import stupaq.compact.TypeDescriptor;

public final class ZoneManagementInfo implements CompactSerializable, Hierarchical, Serializable {
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
          Attribute.MAP_SERIALIZER.writeInstance(out, object.collected);
        }
      };
  private static final long serialVersionUID = 1L;
  private final LocalName localName;
  private final Map<AttributeName, Attribute> collected;

  public ZoneManagementInfo(LocalName localName) {
    this(localName, Maps.<AttributeName, Attribute>newHashMap());
  }

  protected ZoneManagementInfo(LocalName localName, Map<AttributeName, Attribute> collected) {
    this.localName = localName;
    this.collected = collected;
  }

  @Override
  public LocalName localName() {
    return localName;
  }

  @Override
  public TypeDescriptor descriptor() {
    return TypeDescriptor.ZoneManagementInfo;
  }

  public ZoneManagementInfo export() {
    return new ZoneManagementInfo(localName, new HashMap<>(collected));
  }

  public void settableAttributes(Iterable<Attribute> collected, boolean eraseOthers) {
    if (eraseOthers) {
      this.collected.clear();
    }
    for (Attribute attribute : collected) {
      this.collected.put(attribute.getName(), attribute);
    }
  }

  public void recomputedAttribute(Attribute attribute) {
    collected.put(attribute.getName(), attribute);
  }

  public void removeAttribute(AttributeName name) {
    collected.remove(name);
  }

  public Optional<Attribute> getAttribute(AttributeName name) {
    return Optional.fromNullable(collected.get(name));
  }

  // FIXME
  public Collection<Attribute> getPublicAttributes() {
    return FluentIterable.from(collected.values()).filter(new Predicate<Attribute>() {
      @Override
      public boolean apply(Attribute attribute) {
        return !attribute.getName().isSpecial();
      }
    }).toList();
  }

  // FIXME
  public Collection<Attribute> getPrivateAttributes() {
    return FluentIterable.from(collected.values()).filter(new Predicate<Attribute>() {
      @Override
      public boolean apply(Attribute attribute) {
        return attribute.getName().isSpecial();
      }
    }).toList();
  }

  // FIXME
  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    boolean skip = true;
    for (Attribute attribute : collected.values()) {
      builder.append(skip ? "" : "\n").append(attribute.toString());
      skip = false;
    }
    return builder.toString();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ZoneManagementInfo that = (ZoneManagementInfo) o;
    return collected.equals(that.collected) && localName.equals(that.localName) &&
        collected.equals(that.collected);

  }

  @Override
  public int hashCode() {
    int result = localName.hashCode();
    result = 31 * result + collected.hashCode();
    result = 31 * result + collected.hashCode();
    return result;
  }
}
