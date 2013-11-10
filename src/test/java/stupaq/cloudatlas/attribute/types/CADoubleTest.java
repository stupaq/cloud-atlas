package stupaq.cloudatlas.attribute.types;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class CADoubleTest {
  @Test
  public void testConversions() {
    // -> CAString
    assertEquals(new CAString("0.123"), new CADouble(0.123D).to().String());
    // -> CAInteger
    assertEquals(new CAInteger(1L), new CADouble(1.99999D).to().Integer());
  }

  @Test
  public void testOperations() {
    // addition
    assertEquals(new CADouble(3.0), new CADouble(2.0).op().add(new CADouble(1.0)));
    assertEquals(new CADouble(3.0), new CAInteger(2L).op().add(new CADouble(1.0)));
    assertEquals(new CADouble(3.0), new CADouble(2.0).op().add(new CAInteger(1L)));
    // negation
    assertEquals(new CADouble(-3.0), new CADouble(3.0).op().negate());
    // multiplication
    assertEquals(new CADouble(6.0), new CADouble(2.0).op().multiply(new CADouble(3.0)));
    assertEquals(new CADouble(6.0), new CAInteger(2L).op().multiply(new CADouble(3.0)));
    assertEquals(new CADouble(6.0), new CADouble(2.0).op().multiply(new CAInteger(3L)));
    // inversion
    assertEquals(new CADouble(1.0 / 3.0), new CADouble(3.0).op().inverse());
    // round
    assertEquals(new CADouble(1.0), new CADouble(0.5).op().round());
    assertEquals(new CADouble(1.0), new CADouble(0.6).op().round());
    assertEquals(new CADouble(0.0), new CADouble(0.4).op().round());
    // ceil
    assertEquals(new CADouble(1.0), new CADouble(0.5).op().ceil());
    assertEquals(new CADouble(1.0), new CADouble(0.6).op().ceil());
    assertEquals(new CADouble(1.0), new CADouble(0.4).op().ceil());
    assertEquals(new CADouble(0.0), new CADouble(0.0).op().ceil());
    // floor
    assertEquals(new CADouble(0.0), new CADouble(0.5).op().floor());
    assertEquals(new CADouble(0.0), new CADouble(0.6).op().floor());
    assertEquals(new CADouble(0.0), new CADouble(0.4).op().floor());
    assertEquals(new CADouble(1.0), new CADouble(1.0).op().floor());
  }

  @Test
  public void testRelational() {
    assertEquals(new CABoolean(true), new CADouble(1).rel().equalsTo(new CADouble(1)));
    assertEquals(new CABoolean(false), new CADouble(2).rel().equalsTo(new CADouble(1)));
    assertEquals(new CABoolean(false), new CADouble(1).rel().greaterThan(new CADouble(1)));
    assertEquals(new CABoolean(true), new CADouble(2).rel().greaterThan(new CADouble(1)));
  }
}
