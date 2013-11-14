package stupaq.cloudatlas.interpreter.types;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.collect.FluentIterable;

import org.junit.Test;

import stupaq.cloudatlas.attribute.AttributeValue;
import stupaq.cloudatlas.attribute.types.CABoolean;
import stupaq.cloudatlas.attribute.types.CADouble;
import stupaq.cloudatlas.attribute.types.CAInteger;
import stupaq.cloudatlas.attribute.types.CAList;
import stupaq.cloudatlas.attribute.types.CASet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class RCollectionTest {
  @Test
  public void testSemanticsMap() {
    // map
    assertEquals(new RCollection<>(new CAInteger(-2L), new CAInteger(-3L)),
        new RCollection<>(new CAInteger(2L), new CAInteger(3L))
            .map(OperationsTestUtils.<CAInteger>function()));
  }

  @Test
  public void testSemanticsZip() {
    // zip
    assertEquals(new RCollection<>(new CAInteger(3L), new CAInteger(4L)),
        new RCollection<>(new CAInteger(5L), new CAInteger(3L))
            .zip(new RCollection<>(new CAInteger(2L), new CAInteger(-1L)),
                OperationsTestUtils.integerOp()));
    assertEquals(new RCollection<>(new CAInteger(3L), new CAInteger(1L)),
        new RCollection<>(new CAInteger(5L), new CAInteger(3L))
            .zip(new RSingle<>(new CAInteger(2L)), OperationsTestUtils.integerOp()));
    assertEquals(new RCollection<>(new CAInteger(-3L), new CAInteger(-1L)),
        new RSingle<>(new CAInteger(2L))
            .zip(new RCollection<>(new CAInteger(5L), new CAInteger(3L)),
                OperationsTestUtils.integerOp()));
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
    assertEquals(new RSingle<>(new CAList<>(new CAInteger(1))),
        new RCollection<>(new CAInteger(1), new CAInteger(2)).aggregate().first(1));
    assertEquals(new RSingle<>(new CAList<>(new CAInteger(1))),
        new RCollection<>(new CAInteger(1)).aggregate().first(2));
    // last
    assertEquals(new RSingle<>(new CAList<>(new CAInteger(2))),
        new RCollection<>(new CAInteger(1), new CAInteger(2)).aggregate().last(1));
    assertEquals(new RSingle<>(new CAList<>(new CAInteger(1))),
        new RCollection<>(new CAInteger(1)).aggregate().last(2));
    // random
    RCollection<CAInteger> collection =
        new RCollection<>(new CAInteger(1), new CAInteger(2), new CAInteger(3));
    RSingle<CAList<AttributeValue>> sample =
        (RSingle<CAList<AttributeValue>>) collection.aggregate().random(2);
    assertTrue(collection.containsAll(FluentIterable.from(sample.get())
        .transform(new Function<AttributeValue, Optional<AttributeValue>>() {
          @Override
          public Optional<AttributeValue> apply(AttributeValue elem) {
            return Optional.of(elem);
          }
        }).toList()));
    assertEquals(new CAInteger(2), sample.get().op().size());
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
