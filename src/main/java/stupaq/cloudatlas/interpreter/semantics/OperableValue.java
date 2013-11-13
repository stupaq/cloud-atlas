package stupaq.cloudatlas.interpreter.semantics;

import stupaq.cloudatlas.attribute.types.CABoolean;
import stupaq.cloudatlas.attribute.types.CADouble;
import stupaq.cloudatlas.attribute.types.CADuration;
import stupaq.cloudatlas.attribute.types.CAInteger;
import stupaq.cloudatlas.attribute.types.CAString;
import stupaq.cloudatlas.attribute.types.CATime;
import stupaq.cloudatlas.interpreter.Value;
import stupaq.cloudatlas.interpreter.errors.OperationNotApplicable;

public interface OperableValue {

  /** CONJUNCTION is defined for: Boolean */
  public Value and(Value value);

  public Value andWith(CABoolean value);

  /** ALTERNATIVE is defined for: Boolean */
  public Value or(Value value);

  public Value orWith(CABoolean value);

  /** CONTRADICTION is defined for: Boolean */
  public Value contradiction();

  /** NEGATION is defined for: Double, Duration, Integer, Time */
  public Value negate();

  /** ADDITION is defined for: Double, Duration, Integer, String, Time */
  public Value add(Value value);

  public Value addTo(CADouble value);

  public Value addTo(CADuration value);

  public Value addTo(CAInteger value);

  public Value addTo(CAString value);

  public Value addTo(CATime value);

  /** MULTIPLICATION is defined for: Double, Integer */
  public Value multiply(Value value);

  public Value multiplyBy(CADouble value);

  public Value multiplyBy(CAInteger value);

  /** INVERSION is defined for: Double, Integer */
  public Value inverse();

  /** REMAINDER is defined for: Integer */
  public Value modulo(Value value);

  public Value remainderOf(CAInteger value);

  /** REGEXP is defined for: String */
  public Value matches(Value value);

  public Value describes(CAString value);

  /** ROUND is defined for: Double, Integer */
  public Value round();

  /** CEIL is defined for: Double, Integer */
  public Value ceil();

  /** FLOOR is defined for: Double, Integer */
  public Value floor();

  /** SIZE is defined for: List, Set, String */
  public Value size();

  Value multiplyBy(CADuration value);

  public static class OperableValueDefault implements OperableValue {

    private <T extends Value> T noOperation(String operation) throws OperationNotApplicable {
      throw new OperationNotApplicable("Operation " + operation + " is not applicable here");
    }

    @Override
    public Value contradiction() {
      return noOperation("!");
    }

    @Override
    public Value and(Value value) {
      return noOperation("&&");
    }

    @Override
    public Value andWith(CABoolean value) {
      return noOperation("&&");
    }

    @Override
    public Value or(Value value) {
      return noOperation("||");
    }

    @Override
    public Value orWith(CABoolean value) {
      return noOperation("||");
    }

    @Override
    public Value add(Value value) {
      return noOperation("+");
    }

    @Override
    public Value addTo(CADouble value) {
      return noOperation("+");
    }

    @Override
    public Value addTo(CADuration value) {
      return noOperation("+");
    }

    @Override
    public Value addTo(CAInteger value) {
      return noOperation("+");
    }

    @Override
    public Value addTo(CAString value) {
      return noOperation("+");
    }

    @Override
    public Value addTo(CATime value) {
      return noOperation("+");
    }

    @Override
    public Value negate() {
      return noOperation("-");
    }

    @Override
    public Value multiply(Value value) {
      return noOperation("*");
    }

    @Override
    public Value multiplyBy(CADouble value) {
      return noOperation("*");
    }

    @Override
    public Value multiplyBy(CADuration value) {
      return noOperation("*");
    }

    @Override
    public Value multiplyBy(CAInteger value) {
      return noOperation("*");
    }

    @Override
    public Value inverse() {
      return noOperation("/");
    }

    @Override
    public Value modulo(Value value) {
      return noOperation("%");
    }

    @Override
    public Value remainderOf(CAInteger value) {
      return noOperation("%");
    }

    @Override
    public Value matches(Value value) {
      return noOperation("REGEXP");
    }

    @Override
    public Value describes(CAString value) {
      return noOperation("REGEXP");
    }

    @Override
    public Value round() {
      return noOperation("round(...)");
    }

    @Override
    public Value ceil() {
      return noOperation("ceil(...)");
    }

    @Override
    public Value floor() {
      return noOperation("floor(...)");
    }

    @Override
    public Value size() {
      return noOperation("size(...)");
    }
  }
}
