package stupaq.compact;

import com.google.common.base.Preconditions;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import stupaq.commons.base.ASCIIString;

public final class CompactSerializers {
  private CompactSerializers() {
  }

  public static final CompactSerializer<ASCIIString> ASCIIString =
      new CompactSerializer<ASCIIString>() {
        @Override
        @Nullable
        public ASCIIString readInstance(ObjectInput in) throws IOException {
          int length = in.readShort();
          if (length < 0) {
            return null;
          } else {
            byte[] asciiStr = new byte[length];
            in.readFully(asciiStr);
            return new ASCIIString(asciiStr);
          }
        }

        @Override
        public void writeInstance(ObjectOutput out, @Nullable ASCIIString object)
            throws IOException {
          if (object == null) {
            out.writeShort(-1);
          } else {
            try {
              byte[] asciiStr = object.toString().getBytes("US-ASCII");
              out.writeShort(asciiStr.length);
              out.write(asciiStr);
            } catch (UnsupportedEncodingException e) {
              throw new IOException("Cannot serialize " + ASCIIString.class.getSimpleName(), e);
            }
          }
        }
      };
  public static final CompactSerializer<Boolean> Boolean = new CompactSerializer<Boolean>() {
    @Override
    @Nullable
    public Boolean readInstance(ObjectInput in) throws IOException {
      return in.readBoolean() ? in.readBoolean() : null;
    }

    @Override
    public void writeInstance(ObjectOutput out, @Nullable Boolean object) throws IOException {
      out.writeBoolean(object != null);
      if (object != null) {
        out.writeBoolean(object);
      }
    }
  };
  public static final CompactSerializer<Long> Long = new CompactSerializer<Long>() {
    @Override
    @Nullable
    public Long readInstance(ObjectInput in) throws IOException {
      return in.readBoolean() ? in.readLong() : null;
    }

    @Override
    public void writeInstance(ObjectOutput out, @Nullable Long object) throws IOException {
      out.writeBoolean(object != null);
      if (object != null) {
        out.writeLong(object);
      }
    }
  };
  public static final CompactSerializer<String> String = new CompactSerializer<String>() {
    @Override
    @Nullable
    public String readInstance(ObjectInput in) throws IOException {
      return in.readBoolean() ? in.readUTF() : null;
    }

    @Override
    public void writeInstance(ObjectOutput out, @Nullable String object) throws IOException {
      out.writeBoolean(object != null);
      if (object != null) {
        out.writeUTF(object);
      }
    }
  };
  public static final CompactSerializer<Double> Double = new CompactSerializer<Double>() {
    @Override
    @Nullable
    public Double readInstance(ObjectInput in) throws IOException {
      return in.readBoolean() ? in.readDouble() : null;
    }

    @Override
    public void writeInstance(ObjectOutput out, @Nullable Double object) throws IOException {
      out.writeBoolean(object != null);
      if (object != null) {
        out.writeDouble(object);
      }
    }
  };

  public static <Element> CompactSerializer<List<Element>> List(
      final CompactSerializer<Element> elementSerializer) {
    return new CompactSerializer<List<Element>>() {
      @Override
      public List<Element> readInstance(ObjectInput in) throws IOException {
        int elements = in.readInt();
        Preconditions.checkState(elements >= 0);
        ArrayList<Element> list = new ArrayList<>(elements);
        for (; elements > 0; --elements) {
          list.add(elementSerializer.readInstance(in));
        }
        return list;
      }

      @Override
      public void writeInstance(ObjectOutput out, List<Element> object) throws IOException {
        out.writeInt(object.size());
        for (Element attribute : object) {
          elementSerializer.writeInstance(out, attribute);
        }
      }
    };
  }
}
