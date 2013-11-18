package stupaq.cloudatlas.interpreter.semantics;

import stupaq.cloudatlas.attribute.AttributeValue;
import stupaq.cloudatlas.attribute.types.CABoolean;
import stupaq.cloudatlas.attribute.types.CADouble;
import stupaq.cloudatlas.attribute.types.CADuration;
import stupaq.cloudatlas.attribute.types.CAInteger;
import stupaq.cloudatlas.attribute.types.CAList;
import stupaq.cloudatlas.attribute.types.CASet;
import stupaq.cloudatlas.attribute.types.CAString;
import stupaq.cloudatlas.attribute.types.CATime;
import stupaq.cloudatlas.interpreter.errors.UndefinedOperationException;

public interface RelationalValue {

  /** this < value */
  public CABoolean lesserThan(AttributeValue value);

  /** this > value */
  public CABoolean greaterThan(CABoolean value);

  public CABoolean greaterThan(CADouble value);

  public CABoolean greaterThan(CADuration value);

  public CABoolean greaterThan(CAInteger value);

  public CABoolean greaterThan(CAString value);

  public CABoolean greaterThan(CATime value);

  /** this == value */
  public CABoolean equalsTo(AttributeValue value);

  public CABoolean equalsTo(CABoolean value);

  public CABoolean equalsTo(CADouble value);

  public CABoolean equalsTo(CADuration value);

  public CABoolean equalsTo(CAInteger value);

  public CABoolean equalsTo(CAList value);

  public CABoolean equalsTo(CASet value);

  public CABoolean equalsTo(CAString value);

  public CABoolean equalsTo(CATime value);

  /** this <= value (derived) */
  public CABoolean lesserOrEqual(AttributeValue value);

  public static class RelationalValueDefault implements RelationalValue {
    private CABoolean notComparable(AttributeValue value) {
      throw new UndefinedOperationException("Cannot compare with: " + value);
    }

    @Override
    public CABoolean lesserThan(AttributeValue value) {
      return notComparable(value);
    }

    @Override
    public CABoolean greaterThan(CABoolean value) {
      return notComparable(value);
    }

    @Override
    public CABoolean greaterThan(CADouble value) {
      return notComparable(value);
    }

    @Override
    public CABoolean greaterThan(CADuration value) {
      return notComparable(value);
    }

    @Override
    public CABoolean greaterThan(CAInteger value) {
      return notComparable(value);
    }

    @Override
    public CABoolean greaterThan(CAString value) {
      return notComparable(value);
    }

    @Override
    public CABoolean greaterThan(CATime value) {
      return notComparable(value);
    }

    @Override
    public CABoolean equalsTo(AttributeValue value) {
      return notComparable(value);
    }

    @Override
    public CABoolean equalsTo(CABoolean value) {
      return notComparable(value);
    }

    @Override
    public CABoolean equalsTo(CADouble value) {
      return notComparable(value);
    }

    @Override
    public CABoolean equalsTo(CADuration value) {
      return notComparable(value);
    }

    @Override
    public CABoolean equalsTo(CAInteger value) {
      return notComparable(value);
    }

    @Override
    public CABoolean equalsTo(CAList value) {
      return notComparable(value);
    }

    @Override
    public CABoolean equalsTo(CASet value) {
      return notComparable(value);
    }

    @Override
    public CABoolean equalsTo(CAString value) {
      return notComparable(value);
    }

    @Override
    public CABoolean equalsTo(CATime value) {
      return notComparable(value);
    }

    @Override
    public CABoolean lesserOrEqual(AttributeValue value) {
      return this.lesserThan(value).op().or(this.equalsTo(value));
    }
  }
}
