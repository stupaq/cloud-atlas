package stupaq.cloudatlas.attribute.types;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class CABooleanTest {
  @Test
  public void testConversions() {
    // -> CAString
    assertEquals(new CAString("true"), new CABoolean(true).to().String());
    assertEquals(new CAString("false"), new CABoolean(false).to().String());
  }
}
