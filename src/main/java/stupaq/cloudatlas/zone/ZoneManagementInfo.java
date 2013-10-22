package stupaq.cloudatlas.zone;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.HashMap;
import java.util.Map;

import stupaq.cloudatlas.attribute.Attribute;
import stupaq.cloudatlas.attribute.AttributeName;
import stupaq.cloudatlas.serialization.ASCIIString;
import stupaq.cloudatlas.serialization.CompactSerializable;
import stupaq.cloudatlas.serialization.SerializationOnly;

public final class ZoneManagementInfo implements CompactSerializable {
  private final ASCIIString localName;
  private final Map<AttributeName, Attribute> attributes;

  @SerializationOnly
  public ZoneManagementInfo() {
    localName = new ASCIIString(null);
    attributes = new HashMap<>();
  }

  public void addAttribute(Attribute attribute) {
    attributes.put(attribute.getName(), attribute);
  }

  @Override
  public void writeFields(ObjectOutput out) throws IOException {
    localName.writeFields(out);
    out.writeInt(attributes.size());
    for (Attribute Attribute : attributes.values()) {
      Attribute.writeFields(out);
    }
  }

  @Override
  public void readFields(ObjectInput in) throws IOException, ClassNotFoundException {
    localName.readFields(in);
    int elements = in.readInt();
    attributes.clear();
    for (; elements > 0; --elements) {
      Attribute attribute = new Attribute();
      attribute.readFields(in);
      attributes.put(attribute.getName(), attribute);
    }
  }

  @Override
  public boolean equals(Object o) {
    ZoneManagementInfo that = (ZoneManagementInfo) o;
    return this == o || !(o == null || getClass() != o.getClass()) && attributes
        .equals(that.attributes) && localName.equals(that.localName);

  }

  @Override
  public int hashCode() {
    int result = localName.hashCode();
    result = 31 * result + attributes.hashCode();
    return result;
  }
}
