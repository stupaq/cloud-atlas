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
    for (Type elem : get()) {
      Preconditions.checkNotNull(elem, getType().getSimpleName() + " cannot contain nulls");
    }
    TypeUtils.assertUniformCollection(get());
  }

  @Override
  public final String toString() {
    return to().String().toString();
  }

  @Override
  public final void readFields(ObjectInput in) throws IOException, ClassNotFoundException {
    get().clear();
    int elements = in.readInt();
    if (elements == 0) {
      return;
    }
    TypeID typeID = TypeID.readInstance(in);
    for (; elements > 0; elements--) {
      Type instance = TypeRegistry.newInstance(typeID);
      instance.readFields(in);
      get().add(instance);
    }
    verifyInvariants();
  }

  @Override
  public final void writeFields(ObjectOutput out) throws IOException {
    out.writeInt(get().size());
    if (get().isEmpty()) {
      return;
    }
    TypeID typeID = TypeRegistry.resolveType(get().iterator().next().getType());
    TypeID.writeInstance(out, typeID);
    for (Type element : get()) {
      element.writeFields(out);
    }
  }

  @Override
  public final int compareTo(AttributeValue o) {
    throw new UndefinedOperationException("Cannot compare: " + getType().getSimpleName());
  }
}
