package stupaq.cloudatlas.attribute;

import com.google.common.base.Preconditions;

import java.io.IOException;
import java.io.ObjectInput;

import stupaq.guava.base.ASCIIString;
import stupaq.cloudatlas.serialization.CompactSerializable;
import stupaq.cloudatlas.serialization.SerializationOnly;

public final class AttributeName extends ASCIIString implements CompactSerializable {
  private static final String RESERVED_PREFIX = "&";

  @SerializationOnly
  public AttributeName() {
    super(null);
  }

  private AttributeName(String name) {
    super(name);
    verifyInvariants();
  }

  public static AttributeName valueOf(String str) throws IllegalArgumentException {
    Preconditions.checkArgument(!str.startsWith(RESERVED_PREFIX),
        "AttributeName cannot start with reserved prefix: " + RESERVED_PREFIX);
    return new AttributeName(str);
  }

  public static AttributeName valueOfReserved(String str) throws IllegalArgumentException {
    Preconditions.checkArgument(str.startsWith(RESERVED_PREFIX),
        "AttributeName must start with reserved prefix: " + RESERVED_PREFIX);
    return new AttributeName(str);
  }

  public boolean isSpecial() {
    return toString().startsWith(RESERVED_PREFIX);
  }

  private void verifyInvariants() throws NullPointerException, IllegalStateException {
    Preconditions.checkNotNull(toString(), "AttributeName cannot be null");
    Preconditions.checkState(!toString().isEmpty(), "AttributeName cannot be empty");
    Preconditions.checkState(toString().trim().equals(toString()),
        "AttributeName cannot have leading or trailing whitespaces");
  }

  @Override
  public void readFields(ObjectInput in) throws IOException {
    super.readFields(in);
    verifyInvariants();
  }
}
