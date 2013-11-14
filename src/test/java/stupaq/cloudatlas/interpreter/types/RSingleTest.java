package stupaq.cloudatlas.interpreter.types;

import org.junit.Test;

import stupaq.cloudatlas.attribute.types.CAInteger;

import static org.junit.Assert.assertEquals;

public class RSingleTest {
  @Test
  public void testSemanticsMap() {
    // map
    assertEquals(new RSingle<>(new CAInteger(-2L)),
        new RSingle<>(new CAInteger(2L)).map(OperationsTestUtils.<CAInteger>function()));
  }

  @Test
  public void testSemanticsZip() {
    // zip
    assertEquals(new RSingle<>(new CAInteger(4L)), new RSingle<>(new CAInteger(5L))
        .zip(new RSingle<>(new CAInteger(1L)), OperationsTestUtils.integerOp()));
    assertEquals(new RCollection<>(new CAInteger(3L), new CAInteger(1L)),
        new RCollection<>(new CAInteger(5L), new CAInteger(3L))
            .zip(new RSingle<>(new CAInteger(2L)),
                OperationsTestUtils.integerOp()));
    assertEquals(new RCollection<>(new CAInteger(-3L), new CAInteger(-1L)),
        new RSingle<>(new CAInteger(2L))
            .zip(new RCollection<>(new CAInteger(5L), new CAInteger(3L)),
                OperationsTestUtils.integerOp()));
  }
}
