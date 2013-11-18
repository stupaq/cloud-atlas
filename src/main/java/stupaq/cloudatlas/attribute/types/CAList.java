package stupaq.cloudatlas.attribute.types;

import com.google.common.collect.Collections2;
import com.google.common.collect.Iterables;

import org.apache.commons.lang.StringUtils;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import stupaq.cloudatlas.attribute.AttributeValue;
import stupaq.cloudatlas.interpreter.errors.OperationNotApplicable;
import stupaq.cloudatlas.interpreter.semantics.ConvertibleValue;
import stupaq.cloudatlas.interpreter.semantics.ConvertibleValue.ConvertibleValueDefault;
import stupaq.cloudatlas.interpreter.semantics.OperableValue;
import stupaq.cloudatlas.interpreter.semantics.OperableValue.OperableValueDefault;
import stupaq.cloudatlas.interpreter.semantics.RelationalValue;
import stupaq.cloudatlas.interpreter.semantics.RelationalValue.RelationalValueDefault;
import stupaq.cloudatlas.serialization.SerializationOnly;
import stupaq.cloudatlas.serialization.TypeID;
import stupaq.cloudatlas.serialization.TypeRegistry;
import stupaq.guava.base.PrimitiveWrapper;

public class CAList<Type extends AttributeValue> extends PrimitiveWrapper<ArrayList<Type>>
    implements AttributeValue {
  @SerializationOnly
  public CAList() {
    this(Collections.<Type>emptySet());
  }

  @SafeVarargs
  public CAList(Type... elements) {
    this(Arrays.asList(elements));
  }

  public CAList(Iterable<Type> elements) {
    super(new ArrayList<Type>());
    Iterables.addAll(getValue(), elements);
    verifyInvariants();
  }

  public List<Type> asImmutableList() {
    return Collections.unmodifiableList(getValue());
  }

  private void verifyInvariants() throws IllegalStateException {
    TypeUtils.assertUniformCollection(getValue());
  }

  @Override
  public String toString() {
    return to().String().toString();
  }

  @Override
  public void readFields(ObjectInput in) throws IOException, ClassNotFoundException {
    getValue().clear();
    int elements = in.readInt();
    if (elements == 0) {
      return;
    }
    TypeID typeID = TypeID.readInstance(in);
    for (; elements > 0; elements--) {
      Type instance = TypeRegistry.newInstance(typeID);
      instance.readFields(in);
      getValue().add(instance);
    }
    verifyInvariants();
  }

  @Override
  public void writeFields(ObjectOutput out) throws IOException {
    out.writeInt(getValue().size());
    if (getValue().isEmpty()) {
      return;
    }
    TypeID typeID = TypeRegistry.resolveType(getValue().get(0).getType());
    TypeID.writeInstance(out, typeID);
    for (Type element : getValue()) {
      element.writeFields(out);
    }
  }

  @Override
  public Class<CAList> getType() {
    return CAList.class;
  }

  @Override
  public RelationalValue rel() {
    return new RelationalImplementation();
  }

  @Override
  public int compareTo(AttributeValue o) {
    throw new OperationNotApplicable("Cannot compare: " + getType().getSimpleName());
  }

  @Override
  public ConvertibleValue to() {
    return new ConvertibleImplementation();
  }

  @Override
  public OperableValue op() {
    return new OperableImplementation();
  }

  private class ConvertibleImplementation extends ConvertibleValueDefault {
    @Override
    public CAList<Type> List() {
      return CAList.this;
    }

    @Override
    public CAString String() {
      return new CAString("[ " + StringUtils
          .join(Collections2.transform(CAList.this.getValue(), new Stringifier()), ", ") + " ]");
    }

    @Override
    public CASet<Type> Set() {
      return new CASet<>(CAList.this.getValue());
    }
  }

  private class OperableImplementation extends OperableValueDefault {
    @Override
    public AttributeValue size() {
      return new CAInteger((long) CAList.this.getValue().size());
    }
  }

  private class RelationalImplementation extends RelationalValueDefault {
    @Override
    public CABoolean equalsTo(AttributeValue value) {
      return value.rel().equalsTo(CAList.this);
    }

    @Override
    public CABoolean equalsTo(CAList value) {
      return new CABoolean(CAList.this.equals(value));
    }
  }
}
