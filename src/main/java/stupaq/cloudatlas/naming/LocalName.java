package stupaq.cloudatlas.naming;

import stupaq.cloudatlas.serialization.ASCIIString;
import stupaq.cloudatlas.serialization.SerializationOnly;

public final class LocalName extends ASCIIString {
  public final static LocalName ROOT = new LocalName(null);

  @SerializationOnly
  public LocalName() {
    super(null);
  }

  private LocalName(String string) {
    super(string);
  }

  public static LocalName valueOf(String string) {
    return (string == null || string.isEmpty() || string.equals("/")) ? ROOT
                                                                      : new LocalName(string);
  }
}
