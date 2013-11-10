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
    assertEquals(new CAInteger(3L), new CAInteger(2L).op().add(new CAInteger(1L)));
    assertEquals(new CADouble(3.0), new CAInteger(2L).op().add(new CADouble(1.0)));
    assertEquals(new CADouble(3.0), new CADouble(2.0).op().add(new CAInteger(1L)));
    // negation
    assertEquals(new CAInteger(-3L), new CAInteger(3L).op().negate());
    // multiplication
    assertEquals(new CAInteger(6L), new CAInteger(2L).op().multiply(new CAInteger(3L)));
    assertEquals(new CADouble(6.0), new CAInteger(2L).op().multiply(new CADouble(3.0)));
    assertEquals(new CADouble(6.0), new CADouble(2.0).op().multiply(new CAInteger(3L)));
    // inversion
    assertEquals(new CADouble(1.0 / 3.0), new CAInteger(3L).op().inverse());
    // modulo
    assertEquals(new CAInteger(2L), new CAInteger(5L).op().modulo(new CAInteger(3L)));
    assertEquals(new CAInteger(1L), new CAInteger(4L).op().modulo(new CAInteger(3L)));
    assertEquals(new CAInteger(0L), new CAInteger(3L).op().modulo(new CAInteger(3L)));
  }

  @Test
  public void testRelational() {
    assertEquals(new CABoolean(true), new CAInteger(1).rel().equalsTo(new CAInteger(1)));
    assertEquals(new CABoolean(false), new CAInteger(2).rel().equalsTo(new CAInteger(1)));
    assertEquals(new CABoolean(false), new CAInteger(1).rel().greaterThan(new CAInteger(1)));
    assertEquals(new CABoolean(true), new CAInteger(2).rel().greaterThan(new CAInteger(1)));
  }
}
