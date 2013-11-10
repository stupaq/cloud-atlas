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
    assertEquals(new CABoolean(false), new CABoolean(true).op().contradiction());
    assertEquals(new CABoolean(true),
        new CABoolean(true).op().contradiction().op().contradiction());
    // conjunction
    assertEquals(new CABoolean(true), new CABoolean(true).op().and(new CABoolean(true)));
    assertEquals(new CABoolean(false), new CABoolean(true).op().and(new CABoolean(false)));
    assertEquals(new CABoolean(false), new CABoolean(false).op().and(new CABoolean(false)));
    assertEquals(new CABoolean(false), new CABoolean(false).op().and(new CABoolean(true)));
    // alternative
    assertEquals(new CABoolean(true), new CABoolean(true).op().or(new CABoolean(true)));
    assertEquals(new CABoolean(true), new CABoolean(true).op().or(new CABoolean(false)));
    assertEquals(new CABoolean(false), new CABoolean(false).op().or(new CABoolean(false)));
    assertEquals(new CABoolean(true), new CABoolean(false).op().or(new CABoolean(true)));
  }

  @Test
  public void testRelational() {
    assertEquals(new CABoolean(false), new CABoolean(true).rel().equalsTo(new CABoolean(false)));
    assertEquals(new CABoolean(true), new CABoolean(true).rel().equalsTo(new CABoolean(true)));
  }
}
