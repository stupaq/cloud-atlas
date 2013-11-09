package stupaq.cloudatlas.attribute.types;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class CATimeTest {
  @Test
  public void testConversions() {
    // -> CATime
    assertEquals(new CAString("2000/01/01 00:00:00.000 CET"),
        CATime.epoch().getConvertible().to_String());
    // CATime#now()
    System.out.println("CATime.now() returned: " + CATime.now().getConvertible().to_String());
  }
}
