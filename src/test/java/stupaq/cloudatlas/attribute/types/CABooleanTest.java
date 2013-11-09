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

  @Test
  public void testOperations() {
    // negation
    assertEquals(new CABoolean(false), new CABoolean(true).operate().contradiction());
    assertEquals(new CABoolean(true),
        new CABoolean(true).operate().contradiction().operate().contradiction());
    // conjunction
    assertEquals(new CABoolean(true), new CABoolean(true).operate().and(new CABoolean(true)));
    assertEquals(new CABoolean(false), new CABoolean(true).operate().and(new CABoolean(false)));
    assertEquals(new CABoolean(false), new CABoolean(false).operate().and(new CABoolean(false)));
    assertEquals(new CABoolean(false), new CABoolean(false).operate().and(new CABoolean(true)));
    // alternative
    assertEquals(new CABoolean(true), new CABoolean(true).operate().or(new CABoolean(true)));
    assertEquals(new CABoolean(true), new CABoolean(true).operate().or(new CABoolean(false)));
    assertEquals(new CABoolean(false), new CABoolean(false).operate().or(new CABoolean(false)));
    assertEquals(new CABoolean(true), new CABoolean(false).operate().or(new CABoolean(true)));
  }
}
