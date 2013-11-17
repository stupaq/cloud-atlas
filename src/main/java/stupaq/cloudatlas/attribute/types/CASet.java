package stupaq.cloudatlas.attribute.types;

import com.google.common.collect.Collections2;

import org.apache.commons.lang.StringUtils;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

import stupaq.cloudatlas.attribute.AttributeValue;
import stupaq.cloudatlas.interpreter.errors.OperationNotApplicable;
import stupaq.cloudatlas.interpreter.semantics.ConvertibleValue;
import stupaq.cloudatlas.interpreter.semantics.ConvertibleValue.ConvertibleValueDefault;
import stupaq.cloudatlas.interpreter.semantics.OperableValue;
import stupaq.cloudatlas.interpreter.semantics.OperableValue.OperableValueDefault;
import stupaq.cloudatlas.interpreter.semantics.RelationalValue;
import stupaq.cloudatlas.interpreter.semantics.RelationalValue.RelationalValueDefault;
import stupaq.cloudatlas.serialization.SerializationOnly;
import stupaq.guava.base.PrimitiveWrapper;

public class CASet<Type extends AttributeValue> extends PrimitiveWrapper<HashSet<Type>>
    implements AttributeValue {
  @SerializationOnly
  public CASet() {
    this(Collections.<Type>emptySet());
  }

  @SafeVarargs
  public CASet(Type... elements) {
    this(Arrays.asList(elements));
  }

  public CASet(Collection<Type> elements) {
    super(new HashSet<Type>());
    getValue().addAll(elements);
    verifyInvariants();
  }

  private void verifyInvariants() {
    TypeUtils.assertUniformCollection(this.getValue());
  }

  @Override
  public void readFields(ObjectInput in) throws IOException, ClassNotFoundException {
    CAList<Type> list = new CAList<>();
    list.readFields(in);
    getValue().addAll(list.asImmutableList());
    verifyInvariants();
  }

  @Override
  public void writeFields(ObjectOutput out) throws IOException {
    CAList<Type> list = new CAList<>(this.getValue());
    list.writeFields(out);
  }

  @Override
  public Class<CASet> getType() {
    return CASet.class;
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

  @Override
  public RelationalValue rel() {
    return new RelationalImplementation();
  }

  private class ConvertibleImplementation extends ConvertibleValueDefault {
    @Override
    public CAList<Type> List() {
      return new CAList<>(CASet.this.getValue());
    }

    @Override
    public CASet<Type> Set() {
      return CASet.this;
    }

    @Override
    public CAString String() {
      return new CAString("{ " + StringUtils
          .join(Collections2.transform(CASet.this.getValue(), new Stringifier()), ", ") + " }");
    }
  }

  private class OperableImplementation extends OperableValueDefault {
    @Override
    public AttributeValue size() {
      return new CAInteger((long) CASet.this.getValue().size());
    }
  }

  private class RelationalImplementation extends RelationalValueDefault {
    @Override
    public CABoolean equalsTo(AttributeValue value) {
      return value.rel().equalsTo(CASet.this);
    }

    @Override
    public CABoolean equalsTo(CASet value) {
      return new CABoolean(CASet.this.equals(value));
    }
  }
}
