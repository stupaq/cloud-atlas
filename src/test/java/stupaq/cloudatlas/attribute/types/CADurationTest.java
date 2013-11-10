package stupaq.cloudatlas.attribute.types;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class CADurationTest {
  @Test
  public void testConversions() {
    // -> CAInteger
    assertEquals(new CAInteger(1337L), new CADuration(1337L).to().Integer());
    // -> CAString
    assertEquals(new CAString("+2 04:32:21.024"), new CADuration(189141024L).to().String());
    assertEquals(new CAString("-2 04:32:21.024"), new CADuration(-189141024L).to().String());
    assertEquals(new CAString("+72 04:05:22.024"), new CADuration(6235522024L).to().String());
    assertEquals(new CAString("-72 04:05:22.024"), new CADuration(-6235522024L).to().String());
  }

  @Test
  public void testOperations() {
    // negation
    assertEquals(new CADuration(1000L), new CADuration(-1000L).op().negate());
    // addition
    assertEquals(new CATime(1000L), new CATime(900L).op().add(new CADuration(100L)));
    assertEquals(new CATime(1000L), new CATime(100L).op().add(new CADuration(900L)));
    assertEquals(new CATime(1000L), new CADuration(900L).op().add(new CATime(100L)));
    assertEquals(new CATime(1000L), new CADuration(100L).op().add(new CATime(900L)));
    assertEquals(new CADuration(1000L), new CADuration(900L).op().add(new CADuration(100L)));
    assertEquals(new CADuration(1000L), new CADuration(100L).op().add(new CADuration(900L)));
  }

  @Test
  public void testRelational() {
    assertEquals(new CABoolean(true), new CADuration(1).rel().equalsTo(new CADuration(1)));
    assertEquals(new CABoolean(false), new CADuration(2).rel().equalsTo(new CADuration(1)));
    assertEquals(new CABoolean(false), new CADuration(1).rel().greaterThan(new CADuration(1)));
    assertEquals(new CABoolean(true), new CADuration(2).rel().greaterThan(new CADuration(1)));
  }
}
