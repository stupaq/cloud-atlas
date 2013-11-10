package stupaq.cloudatlas.attribute.types;

import com.google.common.collect.Collections2;

import org.apache.commons.lang.StringUtils;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import stupaq.cloudatlas.attribute.AttributeValue;
import stupaq.cloudatlas.interpreter.semantics.ConvertibleValue;
import stupaq.cloudatlas.interpreter.semantics.ConvertibleValue.ConvertibleValueDefault;
import stupaq.cloudatlas.interpreter.semantics.OperableValue;
import stupaq.cloudatlas.interpreter.semantics.OperableValue.OperableValueDefault;
import stupaq.cloudatlas.serialization.SerializationOnly;
import stupaq.cloudatlas.serialization.TypeRegistry;

public class CATuple extends ArrayList<AttributeValue> implements AttributeValue {
  @SerializationOnly
  public CATuple() {
    this(Collections.<AttributeValue>emptyList());
  }

  public CATuple(Collection<AttributeValue> elements) {
    super(elements);
  }

  public CATuple(AttributeValue... elements) {
    this(Arrays.asList(elements));
  }

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

  @Override
  public Class<CATuple> getType() {
    return CATuple.class;
  }

  @Override
  public ConvertibleValue to() {
    return new ConvertibleImplementation();
  }

  @Override
  public OperableValue operate() {
    return new OperableValueDefault();
  }

  private class ConvertibleImplementation extends ConvertibleValueDefault {
    @Override
    public CAString String() {
      return new CAString(
          "<< " + StringUtils.join(Collections2.transform(CATuple.this, new Stringifier()), ", ")
          + " >>");
    }

    @Override
    public CATuple Tuple() {
      return CATuple.this;
    }
  }
}
