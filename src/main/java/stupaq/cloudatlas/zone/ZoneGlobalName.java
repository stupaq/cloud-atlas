package stupaq.cloudatlas.zone;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;

import stupaq.cloudatlas.serialization.CompactSerializable;

public class ZoneGlobalName extends ArrayList<ZoneLocalName> implements CompactSerializable {
  @Override
  public void readFields(ObjectInput in) throws IOException, ClassNotFoundException {
    clear();
    int length = in.readInt();
    ensureCapacity(length);
    for (; length > 0; length--) {
      ZoneLocalName localName = new ZoneLocalName();
      localName.readFields(in);
      add(localName);
    }
  }

  @Override
  public void writeFields(ObjectOutput out) throws IOException {
    out.writeInt(size());
    for (ZoneLocalName localName : this) {
      localName.writeFields(out);
    }
  }
}
