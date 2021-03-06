package stupaq.cloudatlas.naming;

import com.google.common.base.Preconditions;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Iterator;

import javax.annotation.concurrent.Immutable;

import stupaq.commons.base.ForwardingWrapper;
import stupaq.compact.CompactInput;
import stupaq.compact.CompactOutput;
import stupaq.compact.CompactSerializable;
import stupaq.compact.CompactSerializer;
import stupaq.compact.SerializableImplementation;
import stupaq.compact.TypeDescriptor;

import static stupaq.cloudatlas.naming.LocalName.getNotRoot;
import static stupaq.cloudatlas.naming.LocalName.getRoot;
import static stupaq.compact.CompactSerializers.List;

@Immutable
public final class GlobalName extends ForwardingWrapper<ArrayList<LocalName>>
    implements CompactSerializable, Iterable<LocalName> {
  public static final String SEPARATOR = "/";
  public static final CompactSerializer<GlobalName> SERIALIZER =
      new CompactSerializer<GlobalName>() {
        @Override
        public GlobalName readInstance(CompactInput in) throws IOException {
          return new GlobalName(List(LocalName.SERIALIZER).readInstance(in));
        }

        @Override
        public void writeInstance(CompactOutput out, GlobalName object) throws IOException {
          List(LocalName.SERIALIZER).writeInstance(out, object.get());
        }
      };

  @SerializableImplementation
  protected GlobalName() {
  }

  protected GlobalName(ArrayList<LocalName> localNames) {
    super(localNames);
    Preconditions.checkArgument(!localNames.isEmpty(), "Global name cannot be empty");
  }

  public GlobalName prefix(int length) {
    return new GlobalName(new ArrayList<>(get().subList(0, length)));
  }

  public GlobalName parent() {
    return prefix(leafLevel());
  }

  public LocalName leaf() {
    return get().get(leafLevel());
  }

  public int leafLevel() {
    return get().size() - 1;
  }

  public GlobalName lca(GlobalName other) {
    Iterator<LocalName> it1 = iterator(), it2 = other.iterator();
    int level = 0;
    while (it1.hasNext() && it2.hasNext() && it1.next().equals(it2.next())) {
      level++;
    }
    return prefix(level);
  }

  public boolean ancestor(GlobalName name) {
    return lca(name).equals(this);
  }

  @Override
  public boolean equals(Object o) {
    return o instanceof GlobalName && super.equals(o);
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

  @Override
  public TypeDescriptor descriptor() {
    return TypeDescriptor.GlobalName;
  }

  public static GlobalName parse(String string) {
    Preconditions.checkNotNull(string);
    Preconditions.checkArgument(!string.isEmpty(), "Global name cannot be empty");
    Preconditions.checkArgument(string.startsWith(SEPARATOR), "Global name must start with /");
    if (string.length() == 1) {
      return new Builder().parent(getRoot()).build();
    } else {
      Preconditions.checkArgument(!string.endsWith(SEPARATOR), "Global name cannot end with /");
      Builder builder = new Builder().parent(getRoot());
      for (String chunk : string.substring(1).split(SEPARATOR)) {
        builder.child(getNotRoot(chunk));
      }
      return builder.build();
    }
  }

  public static class Builder {
    private ArrayDeque<LocalName> chunks;
    private boolean finalized;

    public Builder() {
      chunks = new ArrayDeque<>();
      finalized = false;
    }

    public Builder(Builder other) {
      chunks = new ArrayDeque<>(other.chunks);
      finalized = other.finalized;
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
      ArrayList<LocalName> chunks = new ArrayList<>(this.chunks);
      this.chunks = null;
      return new GlobalName(chunks);
    }
  }
}
