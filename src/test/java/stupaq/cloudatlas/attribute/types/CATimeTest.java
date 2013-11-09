package stupaq.cloudatlas.attribute.types;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class CATimeTest {
  @Test
  public void testConversions() {
    // -> CATime
    assertEquals(new CAString("2000/01/01 00:00:00.000 CET"), CATime.epoch().to().String());
    // CATime#now()
    System.err.println("CATime.now() returned: " + CATime.now().to().String());
  }

  @Test
  public void testOperations() {
    // negation
    assertEquals(new CATime(-12345L), new CATime(12345L).operate().negate());
    // addition
    assertEquals(new CADuration(-1000L),
        new CATime(10000L).operate().add(new CATime(11000L).operate().negate()));
  }
}
