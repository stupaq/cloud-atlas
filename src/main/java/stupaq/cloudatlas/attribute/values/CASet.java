package stupaq.cloudatlas.attribute.values;

import com.google.common.collect.Collections2;

import org.apache.commons.lang.StringUtils;

import java.util.Collections;
import java.util.LinkedHashSet;

import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import stupaq.cloudatlas.attribute.AttributeValue;
import stupaq.cloudatlas.query.semantics.ConvertibleValue;
import stupaq.cloudatlas.query.semantics.ConvertibleValue.ConvertibleValueDefault;
import stupaq.cloudatlas.query.semantics.OperableValue;
import stupaq.cloudatlas.query.semantics.OperableValue.OperableValueDefault;
import stupaq.cloudatlas.query.semantics.RelationalValue;
import stupaq.cloudatlas.query.semantics.RelationalValue.RelationalValueDefault;
import stupaq.cloudatlas.query.typecheck.TypeInfo;
import stupaq.compact.TypeDescriptor;

@Immutable
public final class CASet<Type extends AttributeValue> extends AbstractComposed<Type> {
  public static final Serializer<AttributeValue, CASet<AttributeValue>> SERIALIZER =
      new Serializer<AttributeValue, CASet<AttributeValue>>() {
        @Override
        protected CASet<AttributeValue> newInstance(TypeInfo<AttributeValue> enclosingType,
            @Nullable Iterable<AttributeValue> elements) {
          return new CASet<>(enclosingType, elements);
        }
      };

  public CASet(TypeInfo<Type> enclosingType) {
    super(new LinkedHashSet<Type>(), enclosingType, null);
  }

  public CASet(TypeInfo<Type> enclosingType, Iterable<Type> elements) {
    super(new LinkedHashSet<Type>(), enclosingType, elements);
  }

  public CASet<Type> orEmpty() {
    return isNull() ? new CASet<>(getEnclosingType(), Collections.<Type>emptySet()) : this;
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

  @Override
  public TypeDescriptor descriptor() {
    return TypeDescriptor.CASet;
  }

  private class ConvertibleImplementation extends ConvertibleValueDefault {
    @Override
    public CAList<Type> List() {
      return new CAList<>(getEnclosingType(), isNull() ? null : delegate());
    }

    @Override
    public CASet<Type> Set() {
      return CASet.this;
    }

    @Override
    public CAString String() {
      return new CAString(isNull() ? null
          : "{ " + StringUtils.join(Collections2.transform(delegate(), new Stringifier()), ", ") +
              " }");
    }
  }

  private class OperableImplementation extends OperableValueDefault {
    @Override
    public CAInteger size() {
      return new CAInteger(isNull() ? null : (long) delegate().size());
    }
  }

  private class RelationalImplementation extends RelationalValueDefault {
    @Override
    public CABoolean equalsTo(AttributeValue value) {
      return value.rel().equalsTo(CASet.this);
    }

    @Override
    public CABoolean equalsTo(CASet value) {
      return new CABoolean(isNull(value) ? null : CASet.this.equals(value));
    }
  }
}
