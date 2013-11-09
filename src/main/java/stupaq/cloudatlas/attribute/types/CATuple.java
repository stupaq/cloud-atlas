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
import stupaq.cloudatlas.interpreter.ConvertibleValue;
import stupaq.cloudatlas.interpreter.ConvertibleValue.ConvertibleValueDefault;
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
  public ConvertibleValue getConvertible() {
    return new ConvertibleImplementation();
  }

  private class ConvertibleImplementation extends ConvertibleValueDefault {
    @Override
    public CAString to_String() {
      return new CAString(
          "<< " + StringUtils.join(Collections2.transform(CATuple.this, new Stringifier()), ", ")
          + " >>");
    }

    @Override
    public CATuple to_Tuple() {
      return CATuple.this;
    }
  }
}
