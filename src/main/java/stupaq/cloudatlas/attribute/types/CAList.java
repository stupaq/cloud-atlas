package stupaq.cloudatlas.attribute.types;

import com.google.common.collect.Collections2;
import com.google.common.collect.Iterables;

import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import stupaq.cloudatlas.attribute.AttributeValue;
import stupaq.cloudatlas.interpreter.semantics.ConvertibleValue;
import stupaq.cloudatlas.interpreter.semantics.ConvertibleValue.ConvertibleValueDefault;
import stupaq.cloudatlas.interpreter.semantics.OperableValue;
import stupaq.cloudatlas.interpreter.semantics.OperableValue.OperableValueDefault;
import stupaq.cloudatlas.interpreter.semantics.RelationalValue;
import stupaq.cloudatlas.interpreter.semantics.RelationalValue.RelationalValueDefault;
import stupaq.cloudatlas.serialization.SerializationOnly;

public class CAList<Type extends AttributeValue>
    extends AbstractComposedValue<Type, ArrayList<Type>> implements AttributeValue {
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
    Iterables.addAll(get(), elements);
    verifyInvariants();
  }

  public List<Type> asImmutableList() {
    return Collections.unmodifiableList(get());
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
          .join(Collections2.transform(CAList.this.get(), new Stringifier()), ", ") + " ]");
    }

    @Override
    public CASet<Type> Set() {
      return new CASet<>(CAList.this.get());
    }
  }

  private class OperableImplementation extends OperableValueDefault {
    @Override
    public AttributeValue size() {
      return new CAInteger((long) CAList.this.get().size());
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
