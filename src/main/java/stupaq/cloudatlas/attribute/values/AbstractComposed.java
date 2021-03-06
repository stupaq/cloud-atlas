package stupaq.cloudatlas.attribute.values;

import com.google.common.base.Preconditions;
import com.google.common.collect.ForwardingCollection;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import stupaq.cloudatlas.attribute.AttributeValue;
import stupaq.cloudatlas.query.errors.TypeCheckerException;
import stupaq.cloudatlas.query.errors.UndefinedOperationException;
import stupaq.cloudatlas.query.typecheck.ComposedTypeInfo;
import stupaq.cloudatlas.query.typecheck.TypeInfo;
import stupaq.commons.lang.Fluent;
import stupaq.compact.CompactInput;
import stupaq.compact.CompactOutput;
import stupaq.compact.CompactSerializer;
import stupaq.compact.TypeRegistry;

import static stupaq.compact.CompactSerializers.Collection;
import static stupaq.compact.TypeRegistry.resolveOrThrow;

/** PACKAGE-LOCAL */
abstract class AbstractComposed<Type extends AttributeValue> extends ForwardingCollection<Type>
    implements AttributeValue {
  @Nonnull private final Collection<Type> value;
  @Nonnull private final TypeInfo<Type> enclosingType;
  private final boolean isNull;

  protected AbstractComposed(@Nonnull Collection<Type> newEmpty,
      @Nonnull TypeInfo<Type> enclosingType, @Nullable Iterable<? extends Type> elements) {
    Preconditions.checkNotNull(newEmpty);
    Preconditions.checkArgument(newEmpty.isEmpty(), "Collection not empty");
    Preconditions.checkNotNull(enclosingType);
    this.value = newEmpty;
    this.enclosingType = enclosingType;
    this.isNull = elements == null;
    if (elements != null) {
      for (Type elem : elements) {
        Preconditions.checkNotNull(elem, type() + " cannot contain nulls");
        if (!enclosingType.equals(elem.type())) {
          throw new TypeCheckerException("Collection contains elements of not matching type");
        }
        add(elem);
      }
    }
  }

  @Override
  protected Collection<Type> delegate() {
    return isNull() ? Fluent.<Collection<Type>>raiseNPE() : value;
  }

  @Override
  public final TypeInfo<? extends AttributeValue> type() {
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

  @Override
  public final int compareTo(@Nonnull AttributeValue o) {
    throw new UndefinedOperationException("Cannot compare: " + type());
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
    return isNull == that.isNull && enclosingType.equals(that.enclosingType) &&
        value.equals(that.value);

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
    return (isNull() ? "NULL" : delegate().toString()) + type();
  }

  protected abstract static class Serializer<Type extends AttributeValue, Actual extends AbstractComposed<Type>>
      implements CompactSerializer<Actual> {

    protected abstract Actual newInstance(TypeInfo<Type> enclosingType,
        @Nullable Iterable<Type> elements);

    @Override
    public final Actual readInstance(CompactInput in) throws IOException {
      // This can be either TypeInfo or ComposedTypeInfo, we have to use dynamic dispatch here
      TypeInfo<Type> enclosingType = TypeRegistry.readObject(in);
      int size = in.readInt();
      if (size > 0) {
        CompactSerializer<Type> serializer = resolveOrThrow(enclosingType.aNull().descriptor());
        return newInstance(enclosingType, Collection(serializer).readInstance(in));
      } else {
        return newInstance(enclosingType, size == 0 ? Collections.<Type>emptyList() : null);
      }
    }

    @Override
    public final void writeInstance(CompactOutput out, Actual object) throws IOException {
      TypeInfo<?> enclosingType = object.getEnclosingType();
      // This can be either TypeInfo or ComposedTypeInfo, we have to use dynamic dispatch here
      TypeRegistry.writeObject(out, enclosingType);
      int elements = object.isNull() ? -1 : object.size();
      out.writeInt(elements);
      if (elements > 0) {
        CompactSerializer<Type> serializer = resolveOrThrow(enclosingType.aNull().descriptor());
        Collection(serializer).writeInstance(out, object);
      }
    }
  }
}
