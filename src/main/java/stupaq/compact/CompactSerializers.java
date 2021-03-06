package stupaq.compact;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.Nullable;

import stupaq.commons.base.ASCIIString;

public final class CompactSerializers {
  private CompactSerializers() {
  }

  public static final CompactSerializer<ASCIIString> ASCIIString =
      new CompactSerializer<ASCIIString>() {
        @Override
        @Nullable
        public ASCIIString readInstance(CompactInput in) throws IOException {
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
        public void writeInstance(CompactOutput out, @Nullable ASCIIString object)
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
    public Boolean readInstance(CompactInput in) throws IOException {
      return in.readBoolean() ? in.readBoolean() : null;
    }

    @Override
    public void writeInstance(CompactOutput out, @Nullable Boolean object) throws IOException {
      out.writeBoolean(object != null);
      if (object != null) {
        out.writeBoolean(object);
      }
    }
  };
  public static final CompactSerializer<Long> Long = new CompactSerializer<Long>() {
    @Override
    @Nullable
    public Long readInstance(CompactInput in) throws IOException {
      return in.readBoolean() ? in.readLong() : null;
    }

    @Override
    public void writeInstance(CompactOutput out, @Nullable Long object) throws IOException {
      out.writeBoolean(object != null);
      if (object != null) {
        out.writeLong(object);
      }
    }
  };
  public static final CompactSerializer<String> String = new CompactSerializer<String>() {
    @Override
    @Nullable
    public String readInstance(CompactInput in) throws IOException {
      return in.readBoolean() ? in.readUTF() : null;
    }

    @Override
    public void writeInstance(CompactOutput out, @Nullable String object) throws IOException {
      out.writeBoolean(object != null);
      if (object != null) {
        out.writeUTF(object);
      }
    }
  };
  public static final CompactSerializer<Double> Double = new CompactSerializer<Double>() {
    @Override
    @Nullable
    public Double readInstance(CompactInput in) throws IOException {
      return in.readBoolean() ? in.readDouble() : null;
    }

    @Override
    public void writeInstance(CompactOutput out, @Nullable Double object) throws IOException {
      out.writeBoolean(object != null);
      if (object != null) {
        out.writeDouble(object);
      }
    }
  };

  public static <Type> CompactSerializer<Type> ConstantSingleton(final Type instance) {
    return new CompactSerializer<Type>() {
      @Override
      public Type readInstance(CompactInput in) throws IOException {
        // Do nothing
        return instance;
      }

      @Override
      public void writeInstance(CompactOutput out, Type object) throws IOException {
        // Do nothing
      }
    };
  }

  public static <Type> CompactSerializer<Type> Nullable(final CompactSerializer<Type> serializer) {
    return new CompactSerializer<Type>() {
      @Override
      public Type readInstance(CompactInput in) throws IOException {
        return in.readBoolean() ? serializer.readInstance(in) : null;
      }

      @Override
      public void writeInstance(CompactOutput out, Type object) throws IOException {
        out.writeBoolean(object != null);
        if (object != null) {
          serializer.writeInstance(out, object);
        }
      }
    };
  }

  public static <Element> ListCompactSerializer<Element> List(
      final CompactSerializer<Element> elementSerializer) {
    return new ListCompactSerializer<Element>() {
      @Override
      public ArrayList<Element> readInstance(CompactInput in) throws IOException {
        int elements = in.readInt();
        Preconditions.checkState(elements >= 0);
        ArrayList<Element> list = new ArrayList<>(elements);
        for (; elements > 0; --elements) {
          list.add(elementSerializer.readInstance(in));
        }
        return list;
      }

      @Override
      public void writeInstance(CompactOutput out, List<Element> object) throws IOException {
        Collection(elementSerializer).writeInstance(out, object);
      }
    };
  }

  public static <Element> CompactSerializer<Collection<Element>> Collection(
      final CompactSerializer<Element> elementSerializer) {
    return new CompactSerializer<Collection<Element>>() {
      @Override
      public Collection<Element> readInstance(CompactInput in) throws IOException {
        return List(elementSerializer).readInstance(in);
      }

      @Override
      public void writeInstance(CompactOutput out, Collection<Element> object) throws IOException {
        out.writeInt(object.size());
        for (Element attribute : object) {
          elementSerializer.writeInstance(out, attribute);
        }
      }
    };
  }

  public static <Key, Value> CompactSerializer<Map<Key, Value>> Map(
      final CompactSerializer<Key> keySerializer, final CompactSerializer<Value> valueSerializer) {
    return new CompactSerializer<Map<Key, Value>>() {
      @Override
      public Map<Key, Value> readInstance(CompactInput in) throws IOException {
        int size = in.readInt();
        Map<Key, Value> map = Maps.newHashMap();
        while (size-- > 0) {
          map.put(keySerializer.readInstance(in), valueSerializer.readInstance(in));
        }
        return map;
      }

      @Override
      public void writeInstance(CompactOutput out, Map<Key, Value> object) throws IOException {
        out.writeInt(object.size());
        for (Entry<Key, Value> entry : object.entrySet()) {
          keySerializer.writeInstance(out, entry.getKey());
          valueSerializer.writeInstance(out, entry.getValue());
        }
      }
    };
  }

  public static interface ListCompactSerializer<Element> extends CompactSerializer<List<Element>> {
    @Override
    public abstract ArrayList<Element> readInstance(CompactInput in) throws IOException;
  }
}
