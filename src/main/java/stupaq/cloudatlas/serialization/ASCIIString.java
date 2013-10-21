package stupaq.cloudatlas.serialization;

import com.google.common.base.CharMatcher;
import com.google.common.base.Preconditions;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.UnsupportedEncodingException;

public class ASCIIString implements CompactSerializable {
  private static final int NULL_STRING_LENGTH = -1;
  private String string;

  public ASCIIString(String string) {
    this.string = string;
    verifyInvariants();
  }

  public static boolean isASCIIString(String str) {
    return str == null || str.length() < Short.MAX_VALUE && CharMatcher.ASCII.matchesAllOf(str);
  }

  private void verifyInvariants() {
    Preconditions
        .checkState(isASCIIString(string), "ASCII String cannot contain non-ASCII characters");
  }

  @Override
  public void writeFields(ObjectOutput out) throws IOException {
    if (string == null) {
      out.writeShort(NULL_STRING_LENGTH);
    } else {
      try {
        byte[] asciiStr = string.getBytes("US-ASCII");
        out.writeShort(asciiStr.length);
        out.write(asciiStr);
      } catch (UnsupportedEncodingException e) {
        throw new IllegalStateException("Impossible state", e);
      }
    }
  }

  @Override
  public void readFields(ObjectInput in) throws IOException {
    int length = in.readShort();
    if (length == NULL_STRING_LENGTH) {
      string = null;
    } else {
      byte[] asciiStr = new byte[length];
      in.readFully(asciiStr);
      string = new String(asciiStr);
    }
    verifyInvariants();
  }

  @Override
  public String toString() {
    return string;
  }

  @Override
  public boolean equals(Object o) {
    return this == o || !(o == null || getClass() != o.getClass()) && !(string != null ? !string
        .equals(((ASCIIString) o).string) : ((ASCIIString) o).string != null);

  }

  @Override
  public int hashCode() {
    return string != null ? string.hashCode() : 0;
  }
}
