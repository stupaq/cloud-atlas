package stupaq.cloudatlas.attribute.types;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static stupaq.cloudatlas.attribute.types.AttributeTypeTestUtils.Bool;
import static stupaq.cloudatlas.attribute.types.AttributeTypeTestUtils.Str;

public class CABooleanTest {
  @Test
  public void testConversions() {
    // -> CAString
    assertEquals(Str("true"), Bool(true).to().String());
    assertEquals(Str("false"), Bool(false).to().String());
  }

  @Test
  public void testOperations() {
    // negation
    assertEquals(Bool(false), Bool(true).op().contradiction());
    assertEquals(Bool(true), Bool(true).op().contradiction().op().contradiction());
    // conjunction
    assertEquals(Bool(true), Bool(true).op().and(Bool(true)));
    assertEquals(Bool(false), Bool(true).op().and(Bool(false)));
    assertEquals(Bool(false), Bool(false).op().and(Bool(false)));
    assertEquals(Bool(false), Bool(false).op().and(Bool(true)));
    // alternative
    assertEquals(Bool(true), Bool(true).op().or(Bool(true)));
    assertEquals(Bool(true), Bool(true).op().or(Bool(false)));
    assertEquals(Bool(false), Bool(false).op().or(Bool(false)));
    assertEquals(Bool(true), Bool(false).op().or(Bool(true)));
  }

  @Test
  public void testRelational() {
    assertEquals(Bool(false), Bool(true).rel().equalsTo(Bool(false)));
    assertEquals(Bool(true), Bool(true).rel().equalsTo(Bool(true)));
  }
}
