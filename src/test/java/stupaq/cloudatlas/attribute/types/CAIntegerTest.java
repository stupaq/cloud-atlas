package stupaq.cloudatlas.attribute.types;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class CAIntegerTest {
  @Test
  public void testConversions() {
    // -> CAString
    assertEquals(new CAString("1337"), new CAInteger(1337L).getConvertible().to_String());
    // -> CADouble
    assertEquals(new CADouble(1337.0D), new CAInteger(1337L).getConvertible().to_Double());
    // -> CADuration
    assertEquals(new CADuration(1337L), new CAInteger(1337L).getConvertible().to_Duration());
  }
}
