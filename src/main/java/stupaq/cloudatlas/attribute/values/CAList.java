package stupaq.cloudatlas.attribute.values;

import com.google.common.collect.Collections2;

import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import stupaq.cloudatlas.attribute.AttributeValue;
import stupaq.cloudatlas.query.semantics.ConvertibleValue;
import stupaq.cloudatlas.query.semantics.ConvertibleValue.ConvertibleValueDefault;
import stupaq.cloudatlas.query.semantics.OperableValue;
import stupaq.cloudatlas.query.semantics.OperableValue.OperableValueDefault;
import stupaq.cloudatlas.query.semantics.RelationalValue;
import stupaq.cloudatlas.query.semantics.RelationalValue.RelationalValueDefault;
import stupaq.cloudatlas.attribute.types.TypeInfo;

public class CAList<Type extends AttributeValue> extends AbstractComposed<Type, ArrayList<Type>> {
  public CAList(TypeInfo<Type> enclosingType) {
    super(new ArrayList<Type>(), enclosingType, null);
  }

  public CAList(TypeInfo<Type> enclosingType, Iterable<Type> elements) {
    super(new ArrayList<Type>(), enclosingType, elements);
  }

  public List<Type> asImmutableList() {
    return Collections.unmodifiableList(get());
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
      return new CAString(isNull() ? null : "[ " + StringUtils
          .join(Collections2.transform(get(), new Stringifier()), ", ") + " ]");
    }

    @Override
    public CASet<Type> Set() {
      return new CASet<>(getEnclosingType(), isNull() ? null : get());
    }
  }

  private class OperableImplementation extends OperableValueDefault {
    @Override
    public CAInteger size() {
      return new CAInteger(isNull() ? null : (long) get().size());
    }
  }

  private class RelationalImplementation extends RelationalValueDefault {
    @Override
    public CABoolean equalsTo(AttributeValue value) {
      return value.rel().equalsTo(CAList.this);
    }

    @Override
    public CABoolean equalsTo(CAList value) {
      return new CABoolean(isNull(value) ? null : CAList.this.equals(value));
    }
  }
}
