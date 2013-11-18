package stupaq.cloudatlas.attribute.types;

import com.google.common.base.Preconditions;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Collection;

import stupaq.cloudatlas.attribute.AttributeValue;
import stupaq.cloudatlas.interpreter.errors.UndefinedOperationException;
import stupaq.cloudatlas.serialization.TypeID;
import stupaq.cloudatlas.serialization.TypeRegistry;
import stupaq.guava.base.PrimitiveWrapper;

/** PACKAGE-LOCAL */
abstract class AbstractComposedValue<Type extends AttributeValue, Composed extends Collection<Type>>
    extends PrimitiveWrapper<Composed> implements AttributeValue {
  protected AbstractComposedValue(Composed value) {
    super(value);
  }

  protected final void verifyInvariants() throws IllegalStateException {
    for (Type elem : getValue()) {
      Preconditions.checkNotNull(elem, getType().getSimpleName() + " cannot contain nulls");
    }
    TypeUtils.assertUniformCollection(getValue());
  }

  @Override
  public final String toString() {
    return to().String().toString();
  }

  @Override
  public final void readFields(ObjectInput in) throws IOException, ClassNotFoundException {
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
  public final void writeFields(ObjectOutput out) throws IOException {
    out.writeInt(getValue().size());
    if (getValue().isEmpty()) {
      return;
    }
    TypeID typeID = TypeRegistry.resolveType(getValue().iterator().next().getType());
    TypeID.writeInstance(out, typeID);
    for (Type element : getValue()) {
      element.writeFields(out);
    }
  }

  @Override
  public final int compareTo(AttributeValue o) {
    throw new UndefinedOperationException("Cannot compare: " + getType().getSimpleName());
  }
}
