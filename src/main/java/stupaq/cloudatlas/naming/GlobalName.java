package stupaq.cloudatlas.naming;

import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.collect.FluentIterable;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import java.util.Arrays;

import stupaq.cloudatlas.serialization.CompactSerializable;
import stupaq.guava.base.PrimitiveWrapper;

public class GlobalName extends PrimitiveWrapper<ArrayList<LocalName>>
    implements CompactSerializable {
  private GlobalName() {
    super(new ArrayList<LocalName>());
  }

  public static GlobalName parse(String string) {
    Preconditions.checkNotNull(string);
    Preconditions.checkArgument(string.startsWith("/"), "Zone global name must start with /");
    Preconditions.checkArgument(string.length() == 1 || !string.endsWith("/"),
        "Zone global name cannot end with /");
    string = string.substring(1);
    GlobalName globalName = new GlobalName();
    FluentIterable.from(Arrays.asList(string.split("/")))
        .transform(new Function<String, LocalName>() {
          @Override
          public LocalName apply(String s) {
            return LocalName.valueOf(s);
          }
        }).copyInto(globalName.getValue());
    assert !globalName.getValue().isEmpty();
    return globalName;
  }

  public LocalName atLevel(int index) {
    return getValue().get(index);
  }

  public int leafLevel() {
    return getValue().size() - 1;
  }

  public LocalName leaf() {
    return atLevel(leafLevel());
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
}
