package stupaq.cloudatlas.attribute.types;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class CAIntegerTest {
  @Test
  public void testConversions() {
    // -> CAString
    assertEquals(new CAString("1337"), new CAInteger(1337L).to().String());
    // -> CADouble
    assertEquals(new CADouble(1337.0D), new CAInteger(1337L).to().Double());
    // -> CADuration
    assertEquals(new CADuration(1337L), new CAInteger(1337L).to().Duration());
  }

  @Test
  public void testOperations() {
    // addition
    assertEquals(new CAInteger(3L), new CAInteger(2L).operate().add(new CAInteger(1L)));
    assertEquals(new CADouble(3.0), new CAInteger(2L).operate().add(new CADouble(1.0)));
    assertEquals(new CADouble(3.0), new CADouble(2.0).operate().add(new CAInteger(1L)));
    // negation
    assertEquals(new CAInteger(-3L), new CAInteger(3L).operate().negate());
    // multiplication
    assertEquals(new CAInteger(6L), new CAInteger(2L).operate().multiply(new CAInteger(3L)));
    assertEquals(new CADouble(6.0), new CAInteger(2L).operate().multiply(new CADouble(3.0)));
    assertEquals(new CADouble(6.0), new CADouble(2.0).operate().multiply(new CAInteger(3L)));
    // inversion
    assertEquals(new CADouble(1.0 / 3.0), new CAInteger(3L).operate().inverse());
    // modulo
    assertEquals(new CAInteger(2L), new CAInteger(5L).operate().modulo(new CAInteger(3L)));
    assertEquals(new CAInteger(1L), new CAInteger(4L).operate().modulo(new CAInteger(3L)));
    assertEquals(new CAInteger(0L), new CAInteger(3L).operate().modulo(new CAInteger(3L)));
  }
}
