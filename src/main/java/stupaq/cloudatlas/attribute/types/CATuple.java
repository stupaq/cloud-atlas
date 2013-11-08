package stupaq.cloudatlas.attribute.types;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;

import stupaq.cloudatlas.attribute.AttributeValue;
import stupaq.cloudatlas.interpreter.Value;
import stupaq.cloudatlas.serialization.TypeRegistry;

public class CATuple extends ArrayList<AttributeValue> implements AttributeValue, Value {
  @Override
  public void readFields(ObjectInput in) throws IOException, ClassNotFoundException {
    clear();
    for (int elements = in.readInt(); elements > 0; elements--) {
      add(in.readBoolean() ? TypeRegistry.<AttributeValue>readObject(in) : null);
    }
  }

  @Override
  public void writeFields(ObjectOutput out) throws IOException {
    out.writeInt(size());
    for (AttributeValue value : this) {
      out.writeBoolean(value != null);
      if (value != null) {
        TypeRegistry.writeObject(out, value);
      }
    }
  }
}
