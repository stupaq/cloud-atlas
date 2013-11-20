package stupaq.cloudatlas.interpreter.semantics;

import stupaq.cloudatlas.attribute.AttributeValue;
import stupaq.cloudatlas.attribute.types.CABoolean;
import stupaq.cloudatlas.attribute.types.CADouble;
import stupaq.cloudatlas.attribute.types.CADuration;
import stupaq.cloudatlas.attribute.types.CAInteger;
import stupaq.cloudatlas.attribute.types.CAString;
import stupaq.cloudatlas.attribute.types.CATime;
import stupaq.cloudatlas.interpreter.errors.UndefinedOperationException;

public interface OperableValue {

  /** CONJUNCTION is defined for: Boolean */
  public CABoolean and(AttributeValue value);

  /** ALTERNATIVE is defined for: Boolean */
  public CABoolean or(AttributeValue value);

  public CABoolean orWith(CABoolean value);

  /** CONTRADICTION is defined for: Boolean */
  public CABoolean not();

  /** NEGATION is defined for: Double, Duration, Integer, Time */
  public AttributeValue negate();

  /** ADDITION is defined for: Double, Duration, Integer, String, Time */
  public AttributeValue add(AttributeValue value);

  public AttributeValue addTo(CADouble value);

  public AttributeValue addTo(CADuration value);

  public AttributeValue addTo(CAInteger value);

  public AttributeValue addTo(CAString value);

  public AttributeValue addTo(CATime value);

  /** MULTIPLICATION is defined for: Double, Integer, Duration */
  public AttributeValue multiply(AttributeValue value);

  public AttributeValue multiplyBy(CADouble value);

  public AttributeValue multiplyBy(CAInteger value);

  public AttributeValue multiplyBy(CADuration value);

  /** INVERSION is defined for: Double, Integer */
  public CADouble inverse();

  /** REMAINDER is defined for: Integer */
  public CAInteger modulo(AttributeValue value);

  public CAInteger remainderOf(CAInteger value);

  /** REGEXP is defined for: String */
  public CABoolean matches(AttributeValue value);

  public CABoolean describes(CAString value);

  /** ROUND is defined for: Double */
  public CADouble round();

  /** CEIL is defined for: Double */
  public CADouble ceil();

  /** FLOOR is defined for: Double, Integer */
  public CADouble floor();

  /** SIZE is defined for: List, Set, String */
  public CAInteger size();

  public static class OperableValueDefault implements OperableValue {

    private <T extends AttributeValue> T noOperation(String operation)
        throws UndefinedOperationException {
      throw new UndefinedOperationException("Operation " + operation + " is not applicable here");
    }

    @Override
    public CABoolean not() {
      return noOperation("!");
    }

    @Override
    public final CABoolean and(AttributeValue value) {
      return not().op().or(value.op().not()).op().not();
    }

    @Override
    public CABoolean or(AttributeValue value) {
      return noOperation("||");
    }

    @Override
    public CABoolean orWith(CABoolean value) {
      return noOperation("||");
    }

    @Override
    public AttributeValue add(AttributeValue value) {
      return noOperation("+");
    }

    @Override
    public AttributeValue addTo(CADouble value) {
      return noOperation("+");
    }

    @Override
    public AttributeValue addTo(CADuration value) {
      return noOperation("+");
    }

    @Override
    public AttributeValue addTo(CAInteger value) {
      return noOperation("+");
    }

    @Override
    public AttributeValue addTo(CAString value) {
      return noOperation("+");
    }

    @Override
    public AttributeValue addTo(CATime value) {
      return noOperation("+");
    }

    @Override
    public AttributeValue negate() {
      return noOperation("-");
    }

    @Override
    public AttributeValue multiply(AttributeValue value) {
      return noOperation("*");
    }

    @Override
    public AttributeValue multiplyBy(CADouble value) {
      return noOperation("*");
    }

    @Override
    public AttributeValue multiplyBy(CADuration value) {
      return noOperation("*");
    }

    @Override
    public AttributeValue multiplyBy(CAInteger value) {
      return noOperation("*");
    }

    @Override
    public CADouble inverse() {
      return noOperation("/");
    }

    @Override
    public CAInteger modulo(AttributeValue value) {
      return noOperation("%");
    }

    @Override
    public CAInteger remainderOf(CAInteger value) {
      return noOperation("%");
    }

    @Override
    public CABoolean matches(AttributeValue value) {
      return noOperation("REGEXP");
    }

    @Override
    public CABoolean describes(CAString value) {
      return noOperation("REGEXP");
    }

    @Override
    public CADouble round() {
      return noOperation("round(...)");
    }

    @Override
    public CADouble ceil() {
      return noOperation("ceil(...)");
    }

    @Override
    public CADouble floor() {
      return noOperation("floor(...)");
    }

    @Override
    public CAInteger size() {
      return noOperation("size(...)");
    }
  }
}
