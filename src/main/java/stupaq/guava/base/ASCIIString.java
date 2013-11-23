package stupaq.guava.base;

import com.google.common.base.CharMatcher;
import com.google.common.base.Preconditions;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

@Immutable
public class ASCIIString extends ForwardingWrapper<String> {
  public ASCIIString(@Nonnull ASCIIString string) {
    super(string.get());
  }

  public ASCIIString(@Nonnull String value) {
    super(value);
    Preconditions
        .checkState(isASCIIString(get()), "ASCII String cannot contain non-ASCII characters");
  }

  public ASCIIString(@Nonnull byte[] value) {
    this(new String(value));
  }

  public static boolean isASCIIString(String str) {
    return str == null || str.length() < Short.MAX_VALUE && CharMatcher.ASCII.matchesAllOf(str);
  }
}
