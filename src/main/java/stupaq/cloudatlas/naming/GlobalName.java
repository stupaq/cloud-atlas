package stupaq.cloudatlas.naming;

import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.collect.FluentIterable;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;

import stupaq.cloudatlas.serialization.CompactSerializable;
import stupaq.guava.base.PrimitiveWrapper;

public class GlobalName extends PrimitiveWrapper<ArrayList<LocalName>>
    implements CompactSerializable, Iterable<LocalName> {
  public static final String SEPARATOR = "/";

  /** Creates global name referring to the root. */
  private GlobalName() {
    this(new ArrayList<>(Arrays.asList(LocalName.getRoot())));
  }

  /** This is for {@link Builder} only. */
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
      return new GlobalName();
    } else {
      Preconditions.checkArgument(!string.endsWith(SEPARATOR), "Global name cannot end with /");
      string = string.substring(1);
      GlobalName globalName = new GlobalName();
      FluentIterable.from(Arrays.asList(string.split(SEPARATOR)))
          .transform(new Function<String, LocalName>() {
            @Override
            public LocalName apply(String s) {
              return LocalName.getNotRoot(s);
            }
          }).copyInto(globalName.getValue());
      assert !globalName.getValue().isEmpty();
      assert globalName.getValue().get(0).equals(LocalName.getRoot());
      return globalName;
    }
  }

  @Override
  public void readFields(ObjectInput in) throws IOException, ClassNotFoundException {
    getValue().clear();
    int length = in.readInt();
    for (; length > 0; length--) {
      LocalName localName = new LocalName();
      localName.readFields(in);
      getValue().add(localName);
    }
  }

  @Override
  public void writeFields(ObjectOutput out) throws IOException {
    out.writeInt(getValue().size());
    for (LocalName localName : getValue()) {
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
    for (LocalName localName : getValue()) {
      builder.append(localName.toString())
          .append(index == 0 || index + 1 == getValue().size() ? "" : SEPARATOR);
      index++;
    }
    return builder.toString();
  }

  @Override
  public Iterator<LocalName> iterator() {
    return getValue().iterator();
  }

  public LocalName leaf() {
    return getValue().get(leafLevel());
  }

  public int leafLevel() {
    return getValue().size() - 1;
  }

  public static class Builder {
    private ArrayList<LocalName> chunks = new ArrayList<>();
    private boolean finalized = false;

    private Builder() {
    }

    public void add(LocalName chunk) {
      Preconditions.checkState(chunks != null, "Builder already used");
      boolean isRoot = chunk.equals(LocalName.getRoot());
      Preconditions.checkState(!finalized || !isRoot, "Global name already finalized");
      finalized |= isRoot;
      chunks.add(chunk);
    }

    public GlobalName build() {
      Preconditions.checkState(chunks != null, "Builder already used");
      Preconditions.checkState(finalized, "Global name must be finalized");
      Collections.reverse(chunks);
      try {
        return new GlobalName(chunks);
      } finally {
        chunks = null;
      }
    }
  }
}
