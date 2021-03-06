package stupaq.cloudatlas.naming;

import com.google.common.base.Preconditions;

import java.io.IOException;

import javax.annotation.concurrent.Immutable;

import stupaq.cloudatlas.services.zonemanager.hierarchy.ZoneHierarchy.Hierarchical;
import stupaq.compact.CompactInput;
import stupaq.compact.CompactOutput;
import stupaq.compact.CompactSerializable;
import stupaq.compact.CompactSerializer;
import stupaq.compact.CompactSerializers;
import stupaq.compact.TypeDescriptor;

@Immutable
public final class LocalName extends CAIdentifier implements CompactSerializable, Hierarchical {
  public static final CompactSerializer<LocalName> SERIALIZER = new CompactSerializer<LocalName>() {
    @Override
    public LocalName readInstance(CompactInput in) throws IOException {
      return new LocalName(CompactSerializers.ASCIIString.readInstance(in).toString());
    }

    @Override
    public void writeInstance(CompactOutput out, LocalName object) throws IOException {
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

  @Override
  public LocalName localName() {
    return this;
  }
}
