package stupaq.cloudatlas.attribute.values;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import stupaq.cloudatlas.attribute.AttributeValue;

/** PACKAGE-LOCAL */
abstract class AbstractStringBacked extends AbstractAtomic<String> implements AttributeValue {
  public AbstractStringBacked(String value) {
    super(value);
  }

  @Override
  public final void readFields(ObjectInput in) throws IOException, ClassNotFoundException {
    if (in.readBoolean()) {
      set(in.readUTF());
    }
  }

  @Override
  public final void writeFields(ObjectOutput out) throws IOException {
    out.writeBoolean(!isNull());
    if (!isNull()) {
      out.writeUTF(get());
    }
  }
}
