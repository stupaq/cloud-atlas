package stupaq.cloudatlas.naming;

import com.google.common.base.Preconditions;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import javax.annotation.concurrent.Immutable;

import stupaq.compact.CompactSerializable;
import stupaq.compact.CompactSerializer;
import stupaq.compact.CompactSerializers;
import stupaq.compact.TypeDescriptor;
import stupaq.guava.base.ASCIIString;

@Immutable
public final class LocalName extends ASCIIString implements CompactSerializable {
  public static final CompactSerializer<LocalName> SERIALIZER = new CompactSerializer<LocalName>() {
    @Override
    public LocalName readInstance(ObjectInput in) throws IOException {
      return new LocalName(CompactSerializers.ASCIIString.readInstance(in).toString());
    }

    @Override
    public void writeInstance(ObjectOutput out, LocalName object) throws IOException {
      CompactSerializers.ASCIIString.writeInstance(out, object);
    }
  };
  private static final String ROOT_STRING = "/";

  protected LocalName(String string) {
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

  @Override
  public TypeDescriptor descriptor() {
    return TypeDescriptor.LocalName;
  }
}
