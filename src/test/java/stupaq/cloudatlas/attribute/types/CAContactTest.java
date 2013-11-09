package stupaq.cloudatlas.attribute.types;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class CAContactTest {
  @Test
  public void testConversions() {
    // -> CAString
    assertEquals(new CAString("UW1"), new CAContact("UW1").to().String());
    assertEquals(new CAString("UW2"), new CAContact("UW2").to().String());
  }

  @Test
  public void testOperations() {
  }
}
