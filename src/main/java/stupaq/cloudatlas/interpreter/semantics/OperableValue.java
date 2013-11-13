package stupaq.cloudatlas.interpreter.semantics;

import stupaq.cloudatlas.attribute.AttributeValue;
import stupaq.cloudatlas.attribute.types.CABoolean;
import stupaq.cloudatlas.attribute.types.CADouble;
import stupaq.cloudatlas.attribute.types.CADuration;
import stupaq.cloudatlas.attribute.types.CAInteger;
import stupaq.cloudatlas.attribute.types.CAString;
import stupaq.cloudatlas.attribute.types.CATime;
import stupaq.cloudatlas.interpreter.errors.OperationNotApplicable;

public interface OperableValue {

  /** CONJUNCTION is defined for: Boolean */
  public AttributeValue and(AttributeValue value);

  public AttributeValue andWith(CABoolean value);

  /** ALTERNATIVE is defined for: Boolean */
  public AttributeValue or(AttributeValue value);

  public AttributeValue orWith(CABoolean value);

  /** CONTRADICTION is defined for: Boolean */
  public AttributeValue contradiction();

  /** NEGATION is defined for: Double, Duration, Integer, Time */
  public AttributeValue negate();

  /** ADDITION is defined for: Double, Duration, Integer, String, Time */
  public AttributeValue add(AttributeValue value);

  public AttributeValue addTo(CADouble value);

  public AttributeValue addTo(CADuration value);

  public AttributeValue addTo(CAInteger value);

  public AttributeValue addTo(CAString value);

  public AttributeValue addTo(CATime value);

  /** MULTIPLICATION is defined for: Double, Integer */
  public AttributeValue multiply(AttributeValue value);

  public AttributeValue multiplyBy(CADouble value);

  public AttributeValue multiplyBy(CAInteger value);

  /** INVERSION is defined for: Double, Integer */
  public AttributeValue inverse();

  /** REMAINDER is defined for: Integer */
  public AttributeValue modulo(AttributeValue value);

  public AttributeValue remainderOf(CAInteger value);

  /** REGEXP is defined for: String */
  public AttributeValue matches(AttributeValue value);

  public AttributeValue describes(CAString value);

  /** ROUND is defined for: Double, Integer */
  public AttributeValue round();

  /** CEIL is defined for: Double, Integer */
  public AttributeValue ceil();

  /** FLOOR is defined for: Double, Integer */
  public AttributeValue floor();

  /** SIZE is defined for: List, Set, String */
  public AttributeValue size();

  AttributeValue multiplyBy(CADuration value);

  public static class OperableValueDefault implements OperableValue {

    private <T extends AttributeValue> T noOperation(String operation) throws OperationNotApplicable {
      throw new OperationNotApplicable("Operation " + operation + " is not applicable here");
    }

    @Override
    public AttributeValue contradiction() {
      return noOperation("!");
    }

    @Override
    public AttributeValue and(AttributeValue value) {
      return noOperation("&&");
    }

    @Override
    public AttributeValue andWith(CABoolean value) {
      return noOperation("&&");
    }

    @Override
    public AttributeValue or(AttributeValue value) {
      return noOperation("||");
    }

    @Override
    public AttributeValue orWith(CABoolean value) {
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
    public AttributeValue inverse() {
      return noOperation("/");
    }

    @Override
    public AttributeValue modulo(AttributeValue value) {
      return noOperation("%");
    }

    @Override
    public AttributeValue remainderOf(CAInteger value) {
      return noOperation("%");
    }

    @Override
    public AttributeValue matches(AttributeValue value) {
      return noOperation("REGEXP");
    }

    @Override
    public AttributeValue describes(CAString value) {
      return noOperation("REGEXP");
    }

    @Override
    public AttributeValue round() {
      return noOperation("round(...)");
    }

    @Override
    public AttributeValue ceil() {
      return noOperation("ceil(...)");
    }

    @Override
    public AttributeValue floor() {
      return noOperation("floor(...)");
    }

    @Override
    public AttributeValue size() {
      return noOperation("size(...)");
    }
  }
}
