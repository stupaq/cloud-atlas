package stupaq.cloudatlas.naming;

import com.google.common.base.Preconditions;

import stupaq.cloudatlas.serialization.ASCIIString;
import stupaq.cloudatlas.serialization.SerializationOnly;

public class LocalName extends ASCIIString {
  private static final String ROOT_STRING = "/";

  @SerializationOnly
  public LocalName() {
    super(ROOT_STRING);
  }

  private LocalName(String string) {
    super(string);
    Preconditions.checkNotNull(string, "Local name cannot be null");
  }

  public static LocalName getRoot() {
    return new LocalName(ROOT_STRING);
  }

  public static LocalName getNotRoot(String string) {
    Preconditions.checkNotNull(string);
    Preconditions.checkArgument(!string.isEmpty(), "Local name cannot be empty");
    Preconditions.checkArgument(!string.contains(GlobalName.SEPARATOR),
        "Local name cannot contain " + GlobalName.SEPARATOR);
    return new LocalName(string);
  }

  public boolean isRoot() {
    return toString().equals(ROOT_STRING);
  }

  @Override
  public String toString() {
    String result = super.toString();
    return result == null ? ROOT_STRING : result;
  }
}
