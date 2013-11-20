package stupaq.cloudatlas.attribute.types;

import com.google.common.collect.Collections2;

import org.apache.commons.lang.StringUtils;

import java.util.HashSet;

import stupaq.cloudatlas.attribute.AttributeValue;
import stupaq.cloudatlas.interpreter.semantics.ConvertibleValue;
import stupaq.cloudatlas.interpreter.semantics.ConvertibleValue.ConvertibleValueDefault;
import stupaq.cloudatlas.interpreter.semantics.OperableValue;
import stupaq.cloudatlas.interpreter.semantics.OperableValue.OperableValueDefault;
import stupaq.cloudatlas.interpreter.semantics.RelationalValue;
import stupaq.cloudatlas.interpreter.semantics.RelationalValue.RelationalValueDefault;
import stupaq.cloudatlas.interpreter.typecheck.TypeInfo;

public class CASet<Type extends AttributeValue> extends AbstractComposed<Type, HashSet<Type>> {
  public CASet(TypeInfo<Type> enclosingType) {
    super(new HashSet<Type>(), enclosingType, null);
  }

  public CASet(TypeInfo<Type> enclosingType, Iterable<Type> elements) {
    super(new HashSet<Type>(), enclosingType, elements);
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
      return new CAList<>(getEnclosingType(), isNull() ? null : get());
    }

    @Override
    public CASet<Type> Set() {
      return CASet.this;
    }

    @Override
    public CAString String() {
      return new CAString(isNull() ? null : "{ " + StringUtils
          .join(Collections2.transform(get(), new Stringifier()), ", ") + " }");
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
      return value.rel().equalsTo(CASet.this);
    }

    @Override
    public CABoolean equalsTo(CASet value) {
      return new CABoolean(isNull(value) ? null : equals(value));
    }
  }
}
