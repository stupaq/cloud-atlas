package stupaq.cloudatlas.attribute.types;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class CAContactTest {
  @Test
  public void testConversions() {
    // -> CAString
    assertEquals(new CAString("UW1"), new CAContact("UW1").getConvertible().to_String());
    assertEquals(new CAString("UW2"), new CAContact("UW2").getConvertible().to_String());
  }
}
