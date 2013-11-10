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
import stupaq.cloudatlas.interpreter.Value;
import stupaq.cloudatlas.interpreter.errors.OperationNotApplicable;
import stupaq.cloudatlas.interpreter.semantics.ConvertibleValue;
import stupaq.cloudatlas.interpreter.semantics.ConvertibleValue.ConvertibleValueDefault;
import stupaq.cloudatlas.interpreter.semantics.OperableValue;
import stupaq.cloudatlas.interpreter.semantics.OperableValue.OperableValueDefault;
import stupaq.cloudatlas.serialization.SerializationOnly;
import stupaq.cloudatlas.serialization.TypeID;
import stupaq.cloudatlas.serialization.TypeRegistry;

public class CAList<Type extends AttributeValue> extends ArrayList<Type> implements AttributeValue {
  @SerializationOnly
  public CAList() {
    this(Collections.<Type>emptySet());
  }

  @SafeVarargs
  public CAList(Type... elements) {
    this(Arrays.asList(elements));
  }

  public CAList(Collection<Type> elements) {
    super(elements);
    verifyInvariants();
  }

  private void verifyInvariants() throws IllegalStateException {
    TypeUtils.assertUniformCollection(this);
  }

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
    verifyInvariants();
  }

  @Override
  public void writeFields(ObjectOutput out) throws IOException {
    out.writeInt(size());
    if (isEmpty()) {
      return;
    }
    TypeID typeID = TypeRegistry.resolveType(get(0).getType());
    TypeID.writeInstance(out, typeID);
    for (Type element : this) {
      element.writeFields(out);
    }
  }

  @Override
  public Class<CAList> getType() {
    return CAList.class;
  }

  @Override
  public int compareTo(Value o) {
    throw new OperationNotApplicable("Cannot compare: " + getType().getSimpleName());
  }

  @Override
  public ConvertibleValue to() {
    return new ConvertibleImplementation();
  }

  @Override
  public OperableValue operate() {
    return new OperableImplementation();
  }

  private class ConvertibleImplementation extends ConvertibleValueDefault {
    @Override
    public CAList<Type> List() {
      return CAList.this;
    }

    @Override
    public CAString String() {
      return new CAString(
          "[ " + StringUtils.join(Collections2.transform(CAList.this, new Stringifier()), ", ")
          + " ]");
    }

    @Override
    public CASet<Type> Set() {
      return new CASet<>(CAList.this);
    }
  }

  private class OperableImplementation extends OperableValueDefault {
    @Override
    public Value size() {
      return new CAInteger((long) CAList.this.size());
    }
  }
}
