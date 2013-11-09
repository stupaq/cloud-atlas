package stupaq.cloudatlas.attribute.types;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class CADoubleTest {
  @Test
  public void testConversions() {
    // -> CAString
    assertEquals(new CAString("0.123"), new CADouble(0.123D).getConvertible().to_String());
    // -> CAInteger
    assertEquals(new CAInteger(1L), new CADouble(1.99999D).getConvertible().to_Integer());
  }
}
