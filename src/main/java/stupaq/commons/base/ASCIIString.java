package stupaq.commons.base;

import com.google.common.base.CharMatcher;
import com.google.common.base.Preconditions;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

import stupaq.compact.SerializationConstructor;

@Immutable
public class ASCIIString extends ForwardingWrapper<String> {
  @SerializationConstructor
  protected ASCIIString() {
  }

  public ASCIIString(@Nonnull ASCIIString string) {
    super(string.get());
  }

  public ASCIIString(@Nonnull String value) {
    super(value);
    verifyInvariants();
  }

  public ASCIIString(@Nonnull byte[] value) {
    this(new String(value));
  }

  private void verifyInvariants() throws IllegalStateException {
    Preconditions.checkState(get().length() < Short.MAX_VALUE);
    Preconditions.checkState(CharMatcher.ASCII.matchesAllOf(get()));
  }
}
