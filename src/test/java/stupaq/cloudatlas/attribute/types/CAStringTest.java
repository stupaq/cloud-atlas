package stupaq.cloudatlas.attribute.types;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class CAStringTest {
  @Test
  public void testConversions() {
    // -> CABoolean
    assertEquals(new CABoolean(true), new CAString("true").to().Boolean());
    assertEquals(new CABoolean(false), new CAString("false").to().Boolean());
    // -> CAInteger
    assertEquals(new CAInteger(1337L), new CAString("1337").to().Integer());
    // -> CADouble
    assertEquals(new CADouble(1337.33D), new CAString("1337.33").to().Double());
    // -> CAContact
    assertEquals(new CAContact("UW1"), new CAString("UW1").to().Contact());
    // -> CAString
    assertEquals(new CAString("UW1"), new CAString("UW1").to().String());
    // -> CATime
    assertEquals(CATime.epoch(), new CAString("2000/01/01 00:00:00.000 CET").to().Time());
    // -> CADuration
    assertEquals(new CADuration(189141024L), new CAString("+2 04:32:21.024").to().Duration());
    assertEquals(new CADuration(-189141024L), new CAString("-2 04:32:21.024").to().Duration());
    assertEquals(new CADuration(6235522024L), new CAString("+72 04:05:22.024").to().Duration());
    assertEquals(new CADuration(-6235522024L), new CAString("-72 04:05:22.024").to().Duration());
  }
}
