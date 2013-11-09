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
    assertEquals(new CADouble(3.0), new CADouble(2.0).operate().add(new CADouble(1.0)));
    assertEquals(new CADouble(3.0), new CAInteger(2L).operate().add(new CADouble(1.0)));
    assertEquals(new CADouble(3.0), new CADouble(2.0).operate().add(new CAInteger(1L)));
    // negation
    assertEquals(new CADouble(-3.0), new CADouble(3.0).operate().negate());
    // multiplication
    assertEquals(new CADouble(6.0), new CADouble(2.0).operate().multiply(new CADouble(3.0)));
    assertEquals(new CADouble(6.0), new CAInteger(2L).operate().multiply(new CADouble(3.0)));
    assertEquals(new CADouble(6.0), new CADouble(2.0).operate().multiply(new CAInteger(3L)));
    // inversion
    assertEquals(new CADouble(1.0 / 3.0), new CADouble(3.0).operate().inverse());
    // round
    assertEquals(new CADouble(1.0), new CADouble(0.5).operate().round());
    assertEquals(new CADouble(1.0), new CADouble(0.6).operate().round());
    assertEquals(new CADouble(0.0), new CADouble(0.4).operate().round());
    // ceil
    assertEquals(new CADouble(1.0), new CADouble(0.5).operate().ceil());
    assertEquals(new CADouble(1.0), new CADouble(0.6).operate().ceil());
    assertEquals(new CADouble(1.0), new CADouble(0.4).operate().ceil());
    assertEquals(new CADouble(0.0), new CADouble(0.0).operate().ceil());
    // floor
    assertEquals(new CADouble(0.0), new CADouble(0.5).operate().floor());
    assertEquals(new CADouble(0.0), new CADouble(0.6).operate().floor());
    assertEquals(new CADouble(0.0), new CADouble(0.4).operate().floor());
    assertEquals(new CADouble(1.0), new CADouble(1.0).operate().floor());
  }
}
