package stupaq.cloudatlas.interpreter.types;

import com.google.common.base.Function;

import org.junit.Test;

import java.util.Collection;

import stupaq.cloudatlas.attribute.types.CABoolean;
import stupaq.cloudatlas.attribute.types.CADouble;
import stupaq.cloudatlas.attribute.types.CAInteger;
import stupaq.cloudatlas.attribute.types.CAList;
import stupaq.cloudatlas.attribute.types.CASet;
import stupaq.cloudatlas.interpreter.Value;
import stupaq.cloudatlas.interpreter.semantics.BinaryOperation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class RCollectionTest {
  final static Function<Value, Value> function = new Function<Value, Value>() {
    @Override
    public Value apply(Value value) {
      return value.op().negate();
    }
  };
  final static BinaryOperation<Value, Value, Value> operation =
      new BinaryOperation<Value, Value, Value>() {
        @Override
        public Value apply(Value value1, Value value2) {
          return value1.op().add(value2.op().negate());
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

  @Test
  public void testAggregators() {
    // avg
    assertEquals(new RSingle<>(new CADouble(4)),
        new RCollection<>(new CAInteger(2), new CAInteger(6)).aggregate().avg());
    // sum
    assertEquals(new RSingle<>(new CAInteger(8)),
        new RCollection<>(new CAInteger(2), new CAInteger(6)).aggregate().sum());
    assertEquals(new RSingle<>(new CADouble(8)),
        new RCollection<>(new CADouble(2), new CADouble(6)).aggregate().sum());
    // count
    assertEquals(new RSingle<>(new CAInteger(2)),
        new RCollection<>(new CAInteger(2), new CAInteger(6)).aggregate().count());
    // first
    assertEquals(new RList<>(new CAInteger(1)),
        new RCollection<>(new CAInteger(1), new CAInteger(2)).aggregate().first(1));
    assertEquals(new RList<>(new CAInteger(1)),
        new RCollection<>(new CAInteger(1)).aggregate().first(2));
    // last
    assertEquals(new RList<>(new CAInteger(2)),
        new RCollection<>(new CAInteger(1), new CAInteger(2)).aggregate().last(1));
    assertEquals(new RList<>(new CAInteger(1)),
        new RCollection<>(new CAInteger(1)).aggregate().last(2));
    // random
    @SuppressWarnings("MismatchedQueryAndUpdateOfCollection") RCollection<CAInteger> collection =
        new RCollection<>(new CAInteger(1), new CAInteger(2), new CAInteger(3));
    assertTrue(collection.containsAll((Collection<?>) collection.aggregate().random(2)));
    assertEquals(new RSingle<>(new CAInteger(2)),
        collection.aggregate().random(2).aggregate().count());
    // min
    assertEquals(new RSingle<>(new CAInteger(2)),
        new RCollection<>(new CAInteger(4), new CAInteger(2), new CAInteger(4), new CAInteger(3))
            .aggregate().min());
    // max
    assertEquals(new RSingle<>(new CAInteger(4)),
        new RCollection<>(new CAInteger(4), new CAInteger(2), new CAInteger(4), new CAInteger(3))
            .aggregate().max());
    // land
    assertEquals(new RSingle<>(new CABoolean(true)),
        new RCollection<>(new CABoolean(true), new CABoolean(true)).aggregate().land());
    assertEquals(new RSingle<>(new CABoolean(false)),
        new RCollection<>(new CABoolean(false), new CABoolean(true)).aggregate().land());
    // lor
    assertEquals(new RSingle<>(new CABoolean(true)),
        new RCollection<>(new CABoolean(true), new CABoolean(true)).aggregate().lor());
    assertEquals(new RSingle<>(new CABoolean(true)),
        new RCollection<>(new CABoolean(false), new CABoolean(true)).aggregate().lor());
    // distinct
    assertEquals(new RList<>(new CAInteger(2), new CAInteger(3)),
        new RList<>(new CAInteger(2), new CAInteger(3), new CAInteger(3), new CAInteger(2))
            .aggregate().distinct());
    // unfold
    assertEquals(new RList<>(new CAInteger(2), new CAInteger(3), new CAInteger(3)),
        new RList<>(new CAList<>(new CAInteger(2), new CAInteger(3)), new CAList<CAInteger>(),
            new CAList<>(new CAInteger(3))).aggregate().unfold());
    assertEquals(new RList<>(new CAInteger(3), new CAInteger(3)),
        new RCollection<>(new CASet<>(new CAInteger(3), new CAInteger(3)), new CASet<CAInteger>(),
            new CASet<>(new CAInteger(3))).aggregate().unfold());
  }
}
