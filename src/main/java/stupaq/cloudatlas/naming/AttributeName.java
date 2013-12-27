package stupaq.cloudatlas.naming;

import com.google.common.base.CharMatcher;
import com.google.common.base.Preconditions;

import java.io.IOException;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

import stupaq.compact.CompactInput;
import stupaq.compact.CompactOutput;
import stupaq.compact.CompactSerializable;
import stupaq.compact.CompactSerializer;
import stupaq.compact.CompactSerializers;
import stupaq.compact.SerializableImplementation;
import stupaq.compact.TypeDescriptor;

@Immutable
public final class AttributeName extends CAIdentifier implements CompactSerializable {
  public static final CompactSerializer<AttributeName> SERIALIZER =
      new CompactSerializer<AttributeName>() {
        @Override
        public AttributeName readInstance(CompactInput in) throws IOException {
          return new AttributeName(CompactSerializers.ASCIIString.readInstance(in).toString());
        }

        @Override
        public void writeInstance(CompactOutput out, AttributeName object) throws IOException {
          CompactSerializers.ASCIIString.writeInstance(out, object);
        }
      };
  public static final String SPECIAL_PREFIX = "&";

  @SerializableImplementation
  protected AttributeName() {
  }

  protected AttributeName(@Nonnull String name) {
    super(name);
    Preconditions.checkState(!toString().isEmpty(), "AttributeName cannot be empty");
    Preconditions.checkState(toString().trim().equals(toString()),
        "AttributeName cannot have leading or trailing whitespaces");
    Preconditions.checkState(!CharMatcher.anyOf(GlobalName.SEPARATOR).matchesAnyOf(toString()),
        "AttributeName cannot contain GlobalName separator");
  }

  public boolean isSpecial() {
    return toString().startsWith(SPECIAL_PREFIX);
  }

  @Override
  public TypeDescriptor descriptor() {
    return TypeDescriptor.AttributeName;
  }

  public static AttributeName fromString(String str) throws IllegalArgumentException {
    Preconditions.checkArgument(!str.startsWith(SPECIAL_PREFIX),
        "AttributeName cannot start with reserved prefix: " + SPECIAL_PREFIX);
    return new AttributeName(str);
  }

  public static AttributeName special(String str) throws IllegalArgumentException {
    Preconditions.checkArgument(str.startsWith(SPECIAL_PREFIX),
        "AttributeName must start with reserved prefix: " + SPECIAL_PREFIX);
    return new AttributeName(str);
  }
}
