package stupaq.cloudatlas.query.typecheck;

import com.google.common.base.Preconditions;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

import stupaq.cloudatlas.attribute.AttributeValue;
import stupaq.cloudatlas.attribute.values.CAList;
import stupaq.cloudatlas.attribute.values.CASet;
import stupaq.compact.CompactInput;
import stupaq.compact.CompactOutput;
import stupaq.compact.CompactSerializer;
import stupaq.compact.TypeDescriptor;

@Immutable
public class ComposedTypeInfo<Atomic extends AttributeValue> extends TypeInfo<Atomic> {
  public static final CompactSerializer<ComposedTypeInfo> SERIALIZER =
      new CompactSerializer<ComposedTypeInfo>() {
        @SuppressWarnings("unchecked")
        @Override
        public ComposedTypeInfo readInstance(CompactInput in) throws IOException {
          return new ComposedTypeInfo<>(in.readBoolean() ? CAList.class : CASet.class,
              TypeInfo.SERIALIZER.readInstance(in));
        }

        @Override
        public void writeInstance(CompactOutput out, ComposedTypeInfo object) throws IOException {
          out.writeBoolean(object.get() == CAList.class);
          TypeInfo.SERIALIZER.writeInstance(out, object);
        }
      };
  @Nonnull private final TypeInfo<? extends AttributeValue> enclosing;

  protected ComposedTypeInfo(Class<Atomic> type,
      @Nonnull TypeInfo<? extends AttributeValue> enclosing) {
    super(type);
    Preconditions.checkNotNull(enclosing);
    this.enclosing = enclosing;
  }

  @Override
  public TypeInfo<? extends AttributeValue> unfold() {
    return enclosing;
  }

  @Override
  public Atomic aNull() {
    try {
      return get().getDeclaredConstructor(TypeInfo.class).newInstance(enclosing);
    } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
        NoSuchMethodException e) {
      throw new IllegalStateException(e);
    }
  }

  @Override
  public String toString() {
    return super.toString() + enclosing.toString();
  }

  @Override
  public boolean equals(Object o) {
    return this == o || !(o == null || getClass() != o.getClass()) && super.equals(o) &&
        enclosing.equals(((ComposedTypeInfo) o).enclosing);

  }

  @Override
  public int hashCode() {
    int result = super.hashCode();
    result = 31 * result + enclosing.hashCode();
    return result;
  }

  @Override
  public TypeDescriptor descriptor() {
    return TypeDescriptor.ComposedTypeInfo;
  }

  public static <Atomic extends AttributeValue, Composed extends AttributeValue> ComposedTypeInfo<Composed> of(
      Class<Composed> type, TypeInfo<Atomic> enclosing) {
    Preconditions.checkArgument(type == CASet.class || type == CAList.class,
        type.getSimpleName() + " is not composed");
    return new ComposedTypeInfo<>(type, enclosing);
  }
}
