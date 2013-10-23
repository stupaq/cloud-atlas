package stupaq.cloudatlas.attribute;

import com.google.common.base.Preconditions;

import java.io.IOException;
import java.io.ObjectInput;

import stupaq.cloudatlas.serialization.ASCIIString;
import stupaq.cloudatlas.serialization.CompactSerializable;
import stupaq.cloudatlas.serialization.SerializationOnly;

public final class AttributeName extends ASCIIString implements CompactSerializable {
  private static final String RESERVED_PREFIX = "&";

  @SerializationOnly
  public AttributeName() {
    super(null);
  }

  public AttributeName(String name) {
    this(name, false);
  }

  public AttributeName(String name, boolean reserved) {
    super(name);
    verifyInvariants();
    Preconditions.checkArgument(reserved || !name.startsWith(RESERVED_PREFIX),
                                "AttributeName cannot start with reserved prefix: "
                                + RESERVED_PREFIX);
  }

  private void verifyInvariants() throws NullPointerException, IllegalStateException {
    Preconditions.checkNotNull(toString(), "AttributeName cannot be null");
    Preconditions.checkState(!toString().isEmpty(), "AttributeName cannot be empty");
  }

  @Override
  public void readFields(ObjectInput in) throws IOException {
    super.readFields(in);
    verifyInvariants();
  }
}
