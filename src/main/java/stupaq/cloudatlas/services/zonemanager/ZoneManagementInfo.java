package stupaq.cloudatlas.services.zonemanager;

import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

import stupaq.cloudatlas.attribute.Attribute;
import stupaq.cloudatlas.naming.AttributeName;
import stupaq.cloudatlas.naming.LocalName;
import stupaq.cloudatlas.services.zonemanager.hierarchy.ZoneHierarchy.Hierarchical;
import stupaq.commons.util.concurrent.LazyCopy;
import stupaq.compact.CompactSerializable;
import stupaq.compact.CompactSerializer;
import stupaq.compact.SerializableImplementation;
import stupaq.compact.TypeDescriptor;

public final class ZoneManagementInfo extends LazyCopy<ZoneManagementInfo>
    implements CompactSerializable, Hierarchical {
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
  private static final long serialVersionUID = 1L;
  private final LocalName localName;
  private Map<AttributeName, Attribute> attributes;
  private transient Set<AttributeName> computed;

  public ZoneManagementInfo(LocalName localName) {
    this(localName, Maps.<AttributeName, Attribute>newHashMap());
  }

  public ZoneManagementInfo(LocalName localName, Map<AttributeName, Attribute> attributes) {
    this.localName = localName;
    this.attributes = attributes;
    computed = Sets.newHashSet();
  }

  @LazyCopyConstructor
  protected ZoneManagementInfo(LocalName localName, Map<AttributeName, Attribute> attributes,
      Set<AttributeName> computed) {
    super(false);
    this.localName = localName;
    this.attributes = attributes;
    this.computed = computed;
  }

  @SerializableImplementation
  private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
    in.defaultReadObject();
    computed = Sets.newHashSet();
  }

  @Override
  protected void deepCopy() {
    attributes = Maps.newHashMap(attributes);
    computed = Sets.newHashSet(computed);
  }

  @Override
  public ZoneManagementInfo export() {
    return new ZoneManagementInfo(localName, attributes, computed);
  }

  @Override
  public LocalName localName() {
    return localName;
  }

  @Override
  public TypeDescriptor descriptor() {
    return TypeDescriptor.ZoneManagementInfo;
  }

  public void clear() {
    ensureCopied();
    attributes.clear();
    computed.clear();
  }

  public void clearComputed() {
    ensureCopied();
    for (AttributeName name : computed) {
      attributes.remove(name);
    }
    computed.clear();
  }

  public void setPrime(Attribute attribute) {
    ensureCopied();
    attributes.put(attribute.getName(), attribute);
    computed.remove(attribute.getName());
  }

  public void setComputed(Attribute attribute) {
    ensureCopied();
    attributes.put(attribute.getName(), attribute);
    computed.add(attribute.getName());
  }

  public void remove(AttributeName name) {
    ensureCopied();
    attributes.remove(name);
    computed.remove(name);
  }

  public Optional<Attribute> get(AttributeName name) {
    return Optional.fromNullable(attributes.get(name));
  }

  public boolean isComputed(AttributeName name) {
    return computed.contains(name);
  }

  // FIXME
  public Collection<Attribute> getPublicAttributes() {
    return FluentIterable.from(attributes.values()).filter(new Predicate<Attribute>() {
      @Override
      public boolean apply(Attribute attribute) {
        return !attribute.getName().isSpecial();
      }
    }).toList();
  }

  // FIXME
  public Collection<Attribute> getPrivateAttributes() {
    return FluentIterable.from(attributes.values()).filter(new Predicate<Attribute>() {
      @Override
      public boolean apply(Attribute attribute) {
        return attribute.getName().isSpecial();
      }
    }).toList();
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
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ZoneManagementInfo that = (ZoneManagementInfo) o;
    return attributes.equals(that.attributes) && computed.equals(that.computed) &&
        localName.equals(that.localName);
  }

  @Override
  public int hashCode() {
    int result = localName.hashCode();
    result = 31 * result + attributes.hashCode();
    result = 31 * result + computed.hashCode();
    return result;
  }
}
