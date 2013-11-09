package stupaq.cloudatlas.attribute.types;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class CAStringTest {
  @Test
  public void testConversions() {
    // -> CABoolean
    assertEquals(new CABoolean(true), new CAString("true").getConvertible().to_Boolean());
    assertEquals(new CABoolean(false), new CAString("false").getConvertible().to_Boolean());
    // -> CAInteger
    assertEquals(new CAInteger(1337L), new CAString("1337").getConvertible().to_Integer());
    // -> CADouble
    assertEquals(new CADouble(1337.33D), new CAString("1337.33").getConvertible().to_Double());
    // -> CATime
    assertEquals(new CATime(),
        new CAString("2000/01/01 00:00:00.000 CET").getConvertible().to_Time());
    assertEquals(new CATime(),
        new CAString("2000/01/01 00:00:00.000 CET").getConvertible().to_Time());
    // -> CADuration
    assertEquals(new CADuration(189141024L),
        new CAString("+2 04:32:21.024").getConvertible().to_Duration());
    assertEquals(new CADuration(-189141024L),
        new CAString("-2 04:32:21.024").getConvertible().to_Duration());
    // -> CAContact
    assertEquals(new CAContact("UW1"), new CAString("UW1").getConvertible().to_Contact());
    // -> CAString
    assertEquals(new CAString("UW1"), new CAString("UW1").getConvertible().to_String());
  }
}
