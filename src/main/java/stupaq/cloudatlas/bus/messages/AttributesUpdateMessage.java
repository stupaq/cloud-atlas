package stupaq.cloudatlas.bus.messages;

import com.google.common.base.Preconditions;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.annotation.concurrent.Immutable;

import stupaq.cloudatlas.attribute.Attribute;
import stupaq.cloudatlas.naming.GlobalName;
import stupaq.compact.CompactSerializer;
import stupaq.compact.TypeDescriptor;

@Immutable
public final class AttributesUpdateMessage extends Message implements Iterable<Attribute> {
  public static final CompactSerializer<AttributesUpdateMessage> SERIALIZER =
      new CompactSerializer<AttributesUpdateMessage>() {
        @Override
        public AttributesUpdateMessage readInstance(ObjectInput in) throws IOException {
          GlobalName zoneName = GlobalName.SERIALIZER.readInstance(in);
          int elements = in.readInt();
          Preconditions.checkState(elements >= 0);
          ArrayList<Attribute> attributes = new ArrayList<>();
          for (; elements > 0; --elements) {
            attributes.add(Attribute.SERIALIZER.readInstance(in));
          }
          boolean override = in.readBoolean();
          return new AttributesUpdateMessage(zoneName, attributes, override);
        }

        @Override
        public void writeInstance(ObjectOutput out, AttributesUpdateMessage object)
            throws IOException {
          GlobalName.SERIALIZER.writeInstance(out, object.zone);
          out.writeInt(object.attributes.size());
          for (Attribute attribute : object.attributes) {
            Attribute.SERIALIZER.writeInstance(out, attribute);
          }
          out.writeBoolean(object.override);
        }
      };
  private final GlobalName zone;
  private final List<Attribute> attributes;
  private final transient boolean override;

  public AttributesUpdateMessage(GlobalName zone, List<Attribute> attributes, boolean override) {
    Preconditions.checkNotNull(zone);
    Preconditions.checkNotNull(attributes);
    this.zone = zone;
    this.attributes = attributes;
    this.override = override;
  }

  @Override
  public TypeDescriptor descriptor() {
    return TypeDescriptor.AttributesUpdateMessage;
  }

  public GlobalName getZone() {
    return zone;
  }

  public boolean isOverride() {
    return override;
  }

  @Override
  public Iterator<Attribute> iterator() {
    return attributes.iterator();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    AttributesUpdateMessage that = (AttributesUpdateMessage) o;
    return override == that.override && attributes.equals(that.attributes) &&
        zone.equals(that.zone);

  }

  @Override
  public int hashCode() {
    int result = zone.hashCode();
    result = 31 * result + attributes.hashCode();
    result = 31 * result + (override ? 1 : 0);
    return result;
  }

  @Override
  public String toString() {
    return "AttributesUpdateMessage{" +
        "zone=" + zone +
        ", attributes=" + attributes +
        ", override=" + override +
        '}';
  }
}
