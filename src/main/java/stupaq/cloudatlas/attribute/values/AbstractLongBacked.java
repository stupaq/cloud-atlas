package stupaq.cloudatlas.attribute.values;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import stupaq.cloudatlas.attribute.AttributeValue;

/** PACKAGE-LOCAL */
abstract class AbstractLongBacked extends AbstractAtomic<Long> implements AttributeValue {
  public AbstractLongBacked() {
    this(null);
  }

  public AbstractLongBacked(Long value) {
    super(value);
  }

  @Override
  public final void readFields(ObjectInput in) throws IOException, ClassNotFoundException {
    if (in.readBoolean()) {
      set(in.readLong());
    }
  }

  @Override
  public final void writeFields(ObjectOutput out) throws IOException {
    out.writeBoolean(!isNull());
    if (!isNull()) {
      out.writeLong(get());
    }
  }
}
