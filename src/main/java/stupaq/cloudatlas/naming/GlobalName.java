package stupaq.cloudatlas.naming;

import com.google.common.base.Preconditions;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Iterator;

import stupaq.cloudatlas.serialization.CompactSerializable;
import stupaq.guava.base.ForwardingWrapper;

import static stupaq.cloudatlas.naming.LocalName.getNotRoot;
import static stupaq.cloudatlas.naming.LocalName.getRoot;

public class GlobalName extends ForwardingWrapper<ArrayList<LocalName>>
    implements CompactSerializable, Iterable<LocalName> {
  public static final String SEPARATOR = "/";

  private GlobalName(ArrayList<LocalName> localNames) {
    super(localNames);
    Preconditions.checkArgument(!localNames.isEmpty(), "Global name cannot be empty");
  }

  public static Builder builder() {
    return new Builder();
  }

  public static GlobalName parse(String string) {
    Preconditions.checkNotNull(string);
    Preconditions.checkArgument(!string.isEmpty(), "Global name cannot be empty");
    Preconditions.checkArgument(string.startsWith(SEPARATOR), "Global name must start with /");
    if (string.length() == 1) {
      return builder().parent(getRoot()).build();
    } else {
      Preconditions.checkArgument(!string.endsWith(SEPARATOR), "Global name cannot end with /");
      Builder builder = new Builder().parent(getRoot());
      for (String chunk : string.substring(1).split(SEPARATOR)) {
        builder.child(getNotRoot(chunk));
      }
      return builder.build();
    }
  }

  @Override
  public void readFields(ObjectInput in) throws IOException, ClassNotFoundException {
    get().clear();
    int length = in.readInt();
    for (; length > 0; length--) {
      LocalName localName = new LocalName();
      localName.readFields(in);
      get().add(localName);
    }
  }

  @Override
  public void writeFields(ObjectOutput out) throws IOException {
    out.writeInt(get().size());
    for (LocalName localName : get()) {
      localName.writeFields(out);
    }
  }

  @Override
  public boolean equals(Object o) {
    return o instanceof GlobalName && super.equals(o);
  }

  @Override
  public int hashCode() {
    return super.hashCode();
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    int index = 0;
    for (LocalName localName : get()) {
      builder.append(localName.toString())
          .append(index == 0 || index + 1 == get().size() ? "" : SEPARATOR);
      index++;
    }
    return builder.toString();
  }

  @Override
  public Iterator<LocalName> iterator() {
    return get().iterator();
  }

  public LocalName leaf() {
    return get().get(leafLevel());
  }

  public int leafLevel() {
    return get().size() - 1;
  }

  public static class Builder {
    private ArrayDeque<LocalName> chunks = new ArrayDeque<>();
    private boolean finalized = false;

    private Builder() {
    }

    private void checkBuilder() {
      Preconditions.checkState(chunks != null, "Builder already used");
    }

    public Builder parent(LocalName chunk) {
      checkBuilder();
      boolean isRoot = chunk.isRoot();
      Preconditions.checkState(!finalized || !isRoot, "Root cannot occur twice");
      finalized |= isRoot;
      chunks.addFirst(chunk);
      return this;
    }

    public Builder child(LocalName chunk) {
      checkBuilder();
      Preconditions.checkState(!chunk.isRoot(), "Root cannot be added as child");
      chunks.addLast(chunk);
      return this;
    }

    public GlobalName build() {
      checkBuilder();
      Preconditions.checkState(finalized, "Global name must start at root");
      try {
        return new GlobalName(new ArrayList<>(chunks));
      } finally {
        chunks = null;
      }
    }
  }
}
