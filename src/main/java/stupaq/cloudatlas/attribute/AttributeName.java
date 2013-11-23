package stupaq.cloudatlas.attribute;

import com.google.common.base.Preconditions;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

import stupaq.compact.CompactSerializable;
import stupaq.compact.CompactSerializer;
import stupaq.compact.TypeDescriptor;
import stupaq.compact.CompactSerializers;
import stupaq.guava.base.ASCIIString;

@Immutable
public final class AttributeName extends ASCIIString implements CompactSerializable {
  public static final CompactSerializer<AttributeName> SERIALIZER =
      new CompactSerializer<AttributeName>() {
        @Override
        public AttributeName readInstance(ObjectInput in) throws IOException {
          return new AttributeName(CompactSerializers.ASCIIString.readInstance(in).toString());
        }

        @Override
        public void writeInstance(ObjectOutput out, AttributeName object) throws IOException {
          CompactSerializers.ASCIIString.writeInstance(out, object);
        }
      };
  private static final String RESERVED_PREFIX = "&";

  protected AttributeName(@Nonnull String name) {
    super(name);
    Preconditions.checkState(!toString().isEmpty(), "AttributeName cannot be empty");
    Preconditions.checkState(toString().trim().equals(toString()),
        "AttributeName cannot have leading or trailing whitespaces");
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

  @Override
  public TypeDescriptor descriptor() {
    return TypeDescriptor.AttributeName;
  }
}
