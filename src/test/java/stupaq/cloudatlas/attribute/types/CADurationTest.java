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
}
