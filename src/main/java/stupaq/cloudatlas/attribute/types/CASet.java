package stupaq.cloudatlas.attribute.types;

import com.google.common.collect.Collections2;

import org.apache.commons.lang.StringUtils;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

import stupaq.cloudatlas.attribute.AttributeValue;
import stupaq.cloudatlas.interpreter.ConvertibleValue;
import stupaq.cloudatlas.interpreter.ConvertibleValue.ConvertibleValueDefault;
import stupaq.cloudatlas.serialization.SerializationOnly;

public class CASet<Type extends AttributeValue> extends HashSet<Type> implements AttributeValue {
  @SerializationOnly
  public CASet() {
    this(Collections.<Type>emptySet());
  }

  public CASet(Type... elements) {
    this(Arrays.asList(elements));
  }

  public CASet(Collection<Type> elements) {
    super(elements);
  }

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

  @Override
  public ConvertibleValue getConvertible() {
    return new ConvertibleImplementation();
  }

  private class ConvertibleImplementation extends ConvertibleValueDefault {
    @Override
    public CASet<Type> to_Set() {
      return CASet.this;
    }

    @Override
    public CAString to_String() {
      return new CAString(
          "{ " + StringUtils.join(Collections2.transform(CASet.this, new Stringifier()), ", ")
          + " }");
    }
  }
}
