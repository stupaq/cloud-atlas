package stupaq.cloudatlas.zone;

import stupaq.cloudatlas.serialization.ASCIIString;
import stupaq.cloudatlas.serialization.SerializationOnly;

public final class ZoneLocalName extends ASCIIString {
  @SerializationOnly
  public ZoneLocalName() {
    super(null);
  }

  public ZoneLocalName(String string) {
    super(string);
  }
}
