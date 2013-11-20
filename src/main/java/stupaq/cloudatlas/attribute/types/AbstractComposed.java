package stupaq.cloudatlas.attribute.types;

import com.google.common.base.Preconditions;
import com.google.common.collect.Iterables;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Collection;

import javax.annotation.Nonnull;

import stupaq.cloudatlas.attribute.AttributeValue;
import stupaq.cloudatlas.interpreter.errors.TypeCheckerException;
import stupaq.cloudatlas.interpreter.errors.UndefinedOperationException;
import stupaq.cloudatlas.interpreter.typecheck.ComposedTypeInfo;
import stupaq.cloudatlas.interpreter.typecheck.TypeInfo;
import stupaq.cloudatlas.serialization.TypeID;
import stupaq.cloudatlas.serialization.TypeRegistry;

/** PACKAGE-LOCAL */
abstract class AbstractComposed<Type extends AttributeValue, Composed extends Collection<Type>>
    implements AttributeValue {
  @Nonnull
  private final Composed value;
  @Nonnull
  private final TypeInfo<Type> enclosingType;
  private boolean isNull;

  protected AbstractComposed(@Nonnull Composed newEmpty, @Nonnull TypeInfo<Type> enclosingType,
      Iterable<? extends Type> source) {
    Preconditions.checkNotNull(newEmpty);
    Preconditions.checkArgument(newEmpty.isEmpty(), "Collection not empty");
    Preconditions.checkNotNull(enclosingType);
    this.value = newEmpty;
    this.enclosingType = enclosingType;
    this.isNull = source == null;
    if (source != null) {
      Iterables.addAll(get(), source);
    }
    verifyInvariants();
  }

  protected final Composed get() {
    if (isNull()) {
      throw new NullPointerException();
    }
    return value;
  }

  @Override
  public final TypeInfo<? extends AttributeValue> getType() {
    return ComposedTypeInfo.of(getClass(), enclosingType);
  }

  @Nonnull
  public final TypeInfo<Type> getEnclosingType() {
    return enclosingType;
  }

  @Override
  public final boolean isNull() {
    return isNull;
  }

  public final boolean isNull(AttributeValue value) {
    return isNull() || value.isNull();
  }

  private void verifyInvariants() throws IllegalStateException {
    if (!isNull()) {
      for (Type elem : get()) {
        Preconditions.checkNotNull(elem, getType() + " cannot contain nulls");
      }
      for (AttributeValue elem : get()) {
        if (!elem.getType().equals(enclosingType)) {
          throw new TypeCheckerException("Collection contains elements of not matching type");
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
      TypeID typeID = TypeRegistry.resolveType(get().iterator().next().getType().get());
      TypeID.writeInstance(out, typeID);
      for (Type element : get()) {
        element.writeFields(out);
      }
    }
  }

  @Override
  public final int compareTo(AttributeValue o) {
    throw new UndefinedOperationException("Cannot compare: " + getType());
  }

  @Override
  public final boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    AbstractComposed that = (AbstractComposed) o;
    return isNull == that.isNull && enclosingType.equals(that.enclosingType) && value
        .equals(that.value);

  }

  @Override
  public final int hashCode() {
    int result = value.hashCode();
    result = 31 * result + enclosingType.hashCode();
    result = 31 * result + (isNull ? 1 : 0);
    return result;
  }

  @Override
  public final String toString() {
    return (isNull() ? "NULL" : get().toString()) + getType();
  }
}
