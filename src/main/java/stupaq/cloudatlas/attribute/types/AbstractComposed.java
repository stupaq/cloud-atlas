package stupaq.cloudatlas.attribute.types;

import com.google.common.base.Preconditions;
import com.google.common.collect.Iterables;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Collection;

import javax.annotation.Nonnull;

import stupaq.cloudatlas.attribute.AttributeValue;
import stupaq.cloudatlas.interpreter.errors.UndefinedOperationException;
import stupaq.cloudatlas.serialization.TypeID;
import stupaq.cloudatlas.serialization.TypeRegistry;

/** PACKAGE-LOCAL */
abstract class AbstractComposed<Type extends AttributeValue, Composed extends Collection<Type>>
    implements AttributeValue {
  @Nonnull
  private final Composed value;
  // FIXME
  private final Class<Type> enclosingType;
  private boolean isNull;

  protected AbstractComposed(@Nonnull Composed newEmpty, Iterable<? extends Type> source) {
    Preconditions.checkNotNull(newEmpty);
    Preconditions.checkArgument(newEmpty.isEmpty(), "Collection not empty");
    this.value = newEmpty;
    this.isNull = source == null;
    if (source != null) {
      Iterables.addAll(get(), source);
    }
    // FIXME
    this.enclosingType = null;
    verifyInvariants();
  }

  protected Composed get() {
    if (isNull()) {
      throw new NullPointerException();
    }
    return value;
  }

  @Override
  public final boolean isNull() {
    return isNull;
  }

  public final boolean isNull(AttributeValue value) {
    return isNull() || value.isNull();
  }

  private final void verifyInvariants() throws IllegalStateException {
    if (!isNull()) {
      for (Type elem : get()) {
        Preconditions.checkNotNull(elem, getType().getSimpleName() + " cannot contain nulls");
      }
      // FIXME
      if (enclosingType != null) {
        for (AttributeValue elem : get()) {
          Preconditions.checkState(elem.getType() == enclosingType,
              "Collection contains elements of not matching type");
        }
      }
    }
  }

  @Override
  public final void readFields(ObjectInput in) throws IOException, ClassNotFoundException {
    int elements = in.readInt();
    if (elements < 0) {
      isNull = true;
    } else if (elements == 0) {
      get().clear();
    } else {
      get().clear();
      TypeID typeID = TypeID.readInstance(in);
      for (; elements > 0; elements--) {
        Type instance = TypeRegistry.newInstance(typeID);
        instance.readFields(in);
        get().add(instance);
      }
      verifyInvariants();
    }
  }

  @Override
  public final void writeFields(ObjectOutput out) throws IOException {
    int elements = isNull() ? -1 : get().size();
    out.writeInt(elements);
    if (elements > 0) {
      TypeID typeID = TypeRegistry.resolveType(get().iterator().next().getType());
      TypeID.writeInstance(out, typeID);
      for (Type element : get()) {
        element.writeFields(out);
      }
    }
  }

  @Override
  public final int compareTo(AttributeValue o) {
    throw new UndefinedOperationException("Cannot compare: " + getType().getSimpleName());
  }

  @Override
  public final String toString() {
    return to().String().toString();
  }
}
