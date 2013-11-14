package stupaq.cloudatlas.interpreter.types;

import org.junit.Test;

import stupaq.cloudatlas.attribute.types.CAInteger;
import stupaq.cloudatlas.interpreter.errors.EvaluationException;

import static org.junit.Assert.assertEquals;

public class RListTest {
  @Test
  public void testSemanticsMap() {
    // map
    assertEquals(new RList<>(new CAInteger(-2L), new CAInteger(-3L)),
        new RList<>(new CAInteger(2L), new CAInteger(3L))
            .map(OperationsTestUtils.<CAInteger>function()));
  }

  @Test(expected = EvaluationException.class)
  public void testSemanticsZip() {
    // zip
    assertEquals(new RList<>(new CAInteger(3L), new CAInteger(4L)),
        new RList<>(new CAInteger(5L), new CAInteger(3L))
            .zip(new RList<>(new CAInteger(2L), new CAInteger(-1L)),
                OperationsTestUtils.integerOp()));
    assertEquals(new RList<>(new CAInteger(3L), new CAInteger(1L)),
        new RList<>(new CAInteger(5L), new CAInteger(3L)).zip(new RSingle<>(new CAInteger(2L)),
            OperationsTestUtils.integerOp()));
    assertEquals(new RList<>(new CAInteger(-3L), new CAInteger(-1L)),
        new RSingle<>(new CAInteger(2L)).zip(new RList<>(new CAInteger(5L), new CAInteger(3L)),
            OperationsTestUtils.integerOp()));
  }
}
