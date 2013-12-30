package stupaq.cloudatlas.services.zonemanager;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Map;
import java.util.Set;

import stupaq.cloudatlas.attribute.Attribute;
import stupaq.cloudatlas.attribute.AttributeValue;
import stupaq.cloudatlas.attribute.values.CABoolean;
import stupaq.cloudatlas.attribute.values.CATime;
import stupaq.cloudatlas.naming.AttributeName;
import stupaq.cloudatlas.naming.LocalName;
import stupaq.cloudatlas.query.typecheck.TypeInfo;
import stupaq.cloudatlas.services.zonemanager.builtins.BuiltinAttributesConfigKeys;
import stupaq.cloudatlas.services.zonemanager.hierarchy.ZoneHierarchy.Hierarchical;
import stupaq.cloudatlas.time.GTPAdjustable;
import stupaq.cloudatlas.time.GTPOffset;
import stupaq.commons.util.concurrent.LazyCopy;
import stupaq.compact.CompactInput;
import stupaq.compact.CompactOutput;
import stupaq.compact.CompactSerializable;
import stupaq.compact.CompactSerializer;
import stupaq.compact.SerializableImplementation;
import stupaq.compact.TypeDescriptor;

public final class ZoneManagementInfo extends LazyCopy<ZoneManagementInfo>
    implements CompactSerializable, Hierarchical, BuiltinAttributesConfigKeys, GTPAdjustable {
  public static final CompactSerializer<ZoneManagementInfo> SERIALIZER =
      new CompactSerializer<ZoneManagementInfo>() {
        @Override
        public ZoneManagementInfo readInstance(CompactInput in) throws IOException {
          return new ZoneManagementInfo(LocalName.SERIALIZER.readInstance(in),
              Attribute.MAP_SERIALIZER.readInstance(in));
        }

        @Override
        public void writeInstance(CompactOutput out, ZoneManagementInfo object) throws IOException {
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

  public CABoolean isOlderThan(ZoneManagementInfo zmi) {
    return isOlderThan(TIMESTAMP.get(zmi));
  }

  public CABoolean isOlderThan(CATime time) {
    return TIMESTAMP.get(this).rel().lesserThan(time);
  }

  public CABoolean isNewerThan(CATime time) {
    return TIMESTAMP.get(this).rel().greaterThan(time);
  }

  @Override
  public void adjustToLocal(GTPOffset offset) {
    // I've decided not to adjust all attributes of CATime type.
    // It can be easily done (if needed) and might be surprising to the user.
    setPrime(TIMESTAMP.create(offset.toLocal(TIMESTAMP.get(this))));
  }

  public boolean update(ZoneManagementInfo update) {
    if (isOlderThan(update).getOr(false)) {
      Preconditions.checkArgument(localName.equals(update.localName()));
      ensureCopied();
      computed.clear();
      attributes = update.attributes;
      return true;
    }
    return false;
  }

  @Override
  public LocalName localName() {
    return localName;
  }

  @Override
  public TypeDescriptor descriptor() {
    return TypeDescriptor.ZoneManagementInfo;
  }

  public void setPrime(Attribute attribute) {
    ensureCopied();
    attributes.put(attribute.name(), attribute);
    computed.remove(attribute.name());
  }

  public void setComputed(Attribute attribute) {
    ensureCopied();
    attributes.put(attribute.name(), attribute);
    computed.add(attribute.name());
  }

  public void remove(AttributeName name) {
    ensureCopied();
    attributes.remove(name);
    computed.remove(name);
  }

  public void removeOfType(TypeInfo<?> type) {
    ensureCopied();
    for (Attribute attribute : attributes.values()) {
      if (type.matches(attribute.value())) {
        remove(attribute.name());
      }
    }
  }

  public void removeComputed() {
    ensureCopied();
    for (AttributeName name : computed) {
      attributes.remove(name);
    }
    computed.clear();
  }

  public Optional<Attribute> get(AttributeName name) {
    return Optional.fromNullable(attributes.get(name));
  }

  @SuppressWarnings("unchecked")
  public <Expected extends AttributeValue> Optional<Attribute<Expected>> get(AttributeName name,
      TypeInfo<Expected> expected) {
    Attribute attribute = attributes.get(name);
    if (attribute == null || !expected.matches(attribute.value())) {
      return Optional.absent();
    }
    return Optional.of((Attribute<Expected>) attribute);
  }

  public FluentIterable<Attribute> publicAttributes() {
    return FluentIterable.from(attributes.values()).filter(new Predicate<Attribute>() {
      @Override
      public boolean apply(Attribute attribute) {
        return !attribute.name().isSpecial();
      }
    });
  }

  public FluentIterable<Attribute> specialAttributes() {
    return FluentIterable.from(attributes.values()).filter(new Predicate<Attribute>() {
      @Override
      public boolean apply(Attribute attribute) {
        return attribute.name().isSpecial();
      }
    });
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
