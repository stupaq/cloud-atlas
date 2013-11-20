package stupaq.cloudatlas.attribute.types;

import stupaq.cloudatlas.attribute.AttributeValue;
import stupaq.cloudatlas.interpreter.errors.UndefinedOperationException;
import stupaq.cloudatlas.interpreter.semantics.ConvertibleValue;
import stupaq.cloudatlas.interpreter.semantics.ConvertibleValue.ConvertibleValueDefault;
import stupaq.cloudatlas.interpreter.semantics.OperableValue;
import stupaq.cloudatlas.interpreter.semantics.OperableValue.OperableValueDefault;
import stupaq.cloudatlas.interpreter.semantics.RelationalValue;
import stupaq.cloudatlas.interpreter.semantics.RelationalValue.RelationalValueDefault;

public class CAContact extends AbstractStringBacked {
  public CAContact() {
    super(null);
  }

  public CAContact(String value) {
    super(value);
  }

  @Override
  public int compareTo(AttributeValue other) {
    throw new UndefinedOperationException("Cannot compare: " + CAContact.class.getSimpleName());
  }

  @Override
  public ConvertibleValue to() {
    return new ConvertibleImplementation();
  }

  @Override
  public OperableValue op() {
    return new OperableValueDefault();
  }

  @Override
  public RelationalValue rel() {
    return new RelationalValueDefault();
  }

  private class ConvertibleImplementation extends ConvertibleValueDefault {
    @Override
    public CAContact Contact() {
      return CAContact.this;
    }

    @Override
    public CAString String() {
      return new CAString(isNull() ? null : get());
    }
  }
}
