package stupaq.cloudatlas.query.typecheck;

import com.google.common.base.Function;
import com.google.common.base.Preconditions;

import java.io.IOException;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

import stupaq.cloudatlas.attribute.AttributeValue;
import stupaq.cloudatlas.attribute.values.CAList;
import stupaq.cloudatlas.attribute.values.CASet;
import stupaq.cloudatlas.query.errors.TypeCheckerException;
import stupaq.commons.base.ForwardingWrapper;
import stupaq.commons.base.Function2;
import stupaq.compact.CompactInput;
import stupaq.compact.CompactOutput;
import stupaq.compact.CompactSerializable;
import stupaq.compact.CompactSerializer;
import stupaq.compact.TypeDescriptor;
import stupaq.compact.TypeRegistry;

@Immutable
public class TypeInfo<Atomic extends AttributeValue> extends ForwardingWrapper<Class<Atomic>>
    implements CompactSerializable {
  public static final CompactSerializer<TypeInfo> SERIALIZER = new CompactSerializer<TypeInfo>() {
    @SuppressWarnings("unchecked")
    @Override
    public TypeInfo readInstance(CompactInput in) throws IOException {
      return new TypeInfo(TypeRegistry.readObject(in).getClass());
    }

    @Override
    public void writeInstance(CompactOutput out, TypeInfo object) throws IOException {
      TypeRegistry.writeObject(out, object.aNull());
    }
  };

  protected TypeInfo(@Nonnull Class<Atomic> type) {
    super(type);
  }

  public Atomic aNull() {
    try {
      return get().newInstance();
    } catch (InstantiationException | IllegalAccessException e) {
      throw new IllegalStateException(e);
    }
  }

  public boolean matches(AttributeValue value) {
    return equals(value.type());
  }

  @SuppressWarnings("unchecked")
  public TypeInfo<? extends AttributeValue> unfold() {
    throw new TypeCheckerException("Cannot unfold atomic type.");
  }

  @Override
  public String toString() {
    return " : " + get().getSimpleName().replace("CA", "").toLowerCase();
  }

  @Override
  public TypeDescriptor descriptor() {
    return TypeDescriptor.TypeInfo;
  }

  public static <Atomic extends AttributeValue> TypeInfo<Atomic> of(@Nonnull Class<Atomic> type) {
    Preconditions.checkArgument(type != CASet.class && type != CAList.class,
        type.getSimpleName() + " is composed");
    return new TypeInfo<>(type);
  }

  @SuppressWarnings("unchecked")
  public static <Atomic extends AttributeValue, Result extends AttributeValue> TypeInfo<Result> typeof1(
      TypeInfo<Atomic> that, Function<Atomic, Result> function) {
    return (TypeInfo<Result>) function.apply(that.aNull()).type();
  }

  @SuppressWarnings("unchecked")
  public static <Atomic extends AttributeValue, Arg1 extends AttributeValue, Result extends AttributeValue> TypeInfo<Result> typeof2(
      TypeInfo<Atomic> that, TypeInfo<Arg1> other, Function2<Atomic, Arg1, Result> function) {
    return (TypeInfo<Result>) function.apply(that.aNull(), other.aNull()).type();
  }
}
