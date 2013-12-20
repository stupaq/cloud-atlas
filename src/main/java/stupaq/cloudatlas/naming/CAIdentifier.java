package stupaq.cloudatlas.naming;

import com.google.common.base.CharMatcher;
import com.google.common.base.Preconditions;

import javax.annotation.Nonnull;

import stupaq.commons.base.ASCIIString;
import stupaq.compact.SerializationConstructor;

public class CAIdentifier extends ASCIIString {
  public static final CharSequence FORBIDDEN = "`~!@#$%^*(){}[]+=|\\:;'\"<,>.?";

  @SerializationConstructor
  protected CAIdentifier() {
  }

  public CAIdentifier(@Nonnull String value) {
    super(value);
    verifyInvariants();
  }

  private void verifyInvariants() throws IllegalStateException {
    Preconditions.checkState(!CharMatcher.anyOf(FORBIDDEN).matchesAllOf(toString()),
        "Forbidden characters in AttributeName");
  }
}
