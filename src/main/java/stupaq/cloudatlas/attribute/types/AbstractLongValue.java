package stupaq.cloudatlas.attribute.types;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import javax.annotation.Nonnull;

import stupaq.cloudatlas.attribute.AttributeValue;
import stupaq.cloudatlas.serialization.SerializationOnly;
import stupaq.guava.base.PrimitiveWrapper;

/** PACKAGE-LOCAL */
abstract class AbstractLongValue extends PrimitiveWrapper<Long> implements AttributeValue {
  @SerializationOnly
  public AbstractLongValue() {
    this(0L);
  }

  public AbstractLongValue(Long value) {
    super(value);
  }

  @Nonnull
  @Override
  public final Long get() {
    return super.get();
  }

  @Override
  public final void readFields(ObjectInput in) throws IOException, ClassNotFoundException {
    set(in.readLong());
  }

  @Override
  public final void writeFields(ObjectOutput out) throws IOException {
    out.writeLong(get());
  }

  @Override
  public final int compareTo(AttributeValue o) {
    TypeUtils.assertSameType(this, o);
    return get().compareTo(((AbstractLongValue) o).get());
  }

  @Override
  public final String toString() {
    return to().String().toString();
  }
}
