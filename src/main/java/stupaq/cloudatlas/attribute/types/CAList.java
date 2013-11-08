package stupaq.cloudatlas.attribute.types;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;

import stupaq.cloudatlas.attribute.AttributeValue;
import stupaq.cloudatlas.interpreter.Value;
import stupaq.cloudatlas.serialization.TypeID;
import stupaq.cloudatlas.serialization.TypeRegistry;

public class CAList<Type extends AttributeValue> extends ArrayList<Type> implements AttributeValue,
                                                                                    Value {
  @Override
  public void readFields(ObjectInput in) throws IOException, ClassNotFoundException {
    clear();
    int elements = in.readInt();
    if (elements == 0) {
      return;
    }
    TypeID typeID = TypeID.readInstance(in);
    for (; elements > 0; elements--) {
      Type instance = TypeRegistry.newInstance(typeID);
      instance.readFields(in);
      add(instance);
    }
  }

  @Override
  public void writeFields(ObjectOutput out) throws IOException {
    if (isEmpty()) {
      out.writeInt(0);
      return;
    }
    out.write(size());
    TypeID typeID = TypeRegistry.resolveType(get(0).getClass());
    TypeID.writeInstance(out, typeID);
    for (Type element : this) {
      element.writeFields(out);
    }
  }
}
