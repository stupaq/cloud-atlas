package stupaq.cloudatlas.naming;

import com.google.common.base.CharMatcher;
import com.google.common.base.Preconditions;

import javax.annotation.Nonnull;

import stupaq.commons.base.ASCIIString;
import stupaq.compact.SerializableImplementation;

import static com.google.common.base.CharMatcher.DIGIT;
import static com.google.common.base.CharMatcher.anyOf;
import static com.google.common.base.CharMatcher.inRange;
import static stupaq.cloudatlas.naming.AttributeName.SPECIAL_PREFIX;
import static stupaq.cloudatlas.naming.GlobalName.SEPARATOR;

public class CAIdentifier extends ASCIIString {
  public static final CharMatcher ALLOWED_CHARACTERS =
      DIGIT.or(inRange('a', 'z')).or(inRange('A', 'Z'))
          .or(anyOf(SPECIAL_PREFIX + SEPARATOR + "_-"));

  @SerializableImplementation
  protected CAIdentifier() {
  }

  public CAIdentifier(@Nonnull String value) {
    super(value);
    verifyInvariants();
  }

  private void verifyInvariants() throws IllegalStateException {
    Preconditions.checkState(ALLOWED_CHARACTERS.matchesAllOf(toString()),
        "Forbidden characters in CAIdentifier: " + toString());
  }
}
