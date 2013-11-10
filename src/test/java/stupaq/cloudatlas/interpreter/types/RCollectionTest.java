package stupaq.cloudatlas.interpreter.types;

import com.google.common.base.Function;

import org.junit.Test;

import stupaq.cloudatlas.attribute.types.CAInteger;
import stupaq.cloudatlas.interpreter.Value;
import stupaq.cloudatlas.interpreter.semantics.BinaryOperation;

import static org.junit.Assert.assertEquals;

public class RCollectionTest {
  final static Function<Value, Value> function = new Function<Value, Value>() {
    @Override
    public Value apply(Value value) {
      return value.operate().negate();
    }
  };
  final static BinaryOperation<Value, Value, Value> operation =
      new BinaryOperation<Value, Value, Value>() {
        @Override
        public Value apply(Value value1, Value value2) {
          return value1.operate().add(value2.operate().negate());
        }
      };

  @Test
  public void testSemanticsMap() {
    // map
    assertEquals(new RCollection<>(new CAInteger(-2L), new CAInteger(-3L)),
        new RCollection<>(new CAInteger(2L), new CAInteger(3L)).map(RCollectionTest.function));
  }

  @Test
  public void testSemanticsZip() {
    // zip
    assertEquals(new RCollection<>(new CAInteger(3L), new CAInteger(4L)),
        new RCollection<>(new CAInteger(5L), new CAInteger(3L))
            .zip(new RCollection<>(new CAInteger(2L), new CAInteger(-1L)), operation));
    assertEquals(new RCollection<>(new CAInteger(3L), new CAInteger(1L)),
        new RCollection<>(new CAInteger(5L), new CAInteger(3L))
            .zip(new RSingle<>(new CAInteger(2L)), operation));
    assertEquals(new RCollection<>(new CAInteger(-3L), new CAInteger(-1L)),
        new RSingle<>(new CAInteger(2L))
            .zip(new RCollection<>(new CAInteger(5L), new CAInteger(3L)), operation));
  }
}
