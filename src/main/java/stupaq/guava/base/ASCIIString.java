package stupaq.guava.base;

import com.google.common.base.CharMatcher;
import com.google.common.base.Preconditions;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.UnsupportedEncodingException;

import javax.annotation.Nonnull;

import stupaq.cloudatlas.serialization.CompactSerializable;

// TODO break dependency from stupaq.cloudatlas serialization framework
public class ASCIIString extends ForwardingWrapper<String> implements CompactSerializable {
  public ASCIIString(@Nonnull String string) {
    super(string);
    verifyInvariants();
  }

  public static boolean isASCIIString(String str) {
    return str == null || str.length() < Short.MAX_VALUE && CharMatcher.ASCII.matchesAllOf(str);
  }

  private void verifyInvariants() {
    Preconditions
        .checkState(isASCIIString(get()), "ASCII String cannot contain non-ASCII characters");
  }

  @Override
  public void writeFields(ObjectOutput out) throws IOException {
    try {
      byte[] asciiStr = get().getBytes("US-ASCII");
      out.writeShort(asciiStr.length);
      out.write(asciiStr);
    } catch (UnsupportedEncodingException e) {
      throw new IllegalStateException("Impossible state", e);
    }
  }

  @Override
  public void readFields(ObjectInput in) throws IOException {
    int length = in.readShort();
    Preconditions.checkState(length >= 0);
    byte[] asciiStr = new byte[length];
    in.readFully(asciiStr);
    set(new String(asciiStr));
    verifyInvariants();
  }
}
