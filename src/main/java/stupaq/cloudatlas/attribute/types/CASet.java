package stupaq.cloudatlas.attribute.types;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.HashSet;

import stupaq.cloudatlas.attribute.AttributeValue;
import stupaq.cloudatlas.interpreter.Value;

public class CASet<Type extends AttributeValue> extends HashSet<Type> implements AttributeValue,
                                                                                 Value {
  @Override
  public void readFields(ObjectInput in) throws IOException, ClassNotFoundException {
    CAList<Type> list = new CAList<>();
    list.readFields(in);
    addAll(list);
  }

  @Override
  public void writeFields(ObjectOutput out) throws IOException {
    CAList<Type> list = new CAList<>();
    list.addAll(this);
    list.writeFields(out);
  }
}
