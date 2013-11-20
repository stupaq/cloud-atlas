package stupaq.cloudatlas.attribute.types;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static stupaq.cloudatlas.attribute.types.AttributeValueTestUtils.Bool;
import static stupaq.cloudatlas.attribute.types.AttributeValueTestUtils.Str;

public class CABooleanTest {
  @Test
  public void testConversions() {
    // -> CAString
    assertEquals(Str("true"), Bool(true).to().String());
    assertEquals(Str("false"), Bool(false).to().String());
    assertEquals(Str(), Bool().to().String());
  }

  @Test
  public void testOperations() {
    // negation
    assertEquals(Bool(false), Bool(true).op().not());
    assertEquals(Bool(true), Bool(true).op().not().op().not());
    assertEquals(Bool(), Bool().op().not());
    // conjunction
    assertEquals(Bool(true), Bool(true).op().and(Bool(true)));
    assertEquals(Bool(false), Bool(true).op().and(Bool(false)));
    assertEquals(Bool(false), Bool(false).op().and(Bool(false)));
    assertEquals(Bool(false), Bool(false).op().and(Bool(true)));
    assertEquals(Bool(), Bool().op().and(Bool(true)));
    assertEquals(Bool(), Bool(false).op().and(Bool()));
    assertEquals(Bool(), Bool().op().and(Bool()));
    // alternative
    assertEquals(Bool(true), Bool(true).op().or(Bool(true)));
    assertEquals(Bool(true), Bool(true).op().or(Bool(false)));
    assertEquals(Bool(false), Bool(false).op().or(Bool(false)));
    assertEquals(Bool(true), Bool(false).op().or(Bool(true)));
    assertEquals(Bool(), Bool().op().or(Bool(true)));
    assertEquals(Bool(), Bool(false).op().or(Bool()));
    assertEquals(Bool(), Bool().op().or(Bool()));
  }

  @Test
  public void testRelational() {
    assertEquals(Bool(false), Bool(true).rel().equalsTo(Bool(false)));
    assertEquals(Bool(true), Bool(true).rel().equalsTo(Bool(true)));
    assertEquals(Bool(), Bool().rel().equalsTo(Bool(true)));
    assertEquals(Bool(), Bool(true).rel().equalsTo(Bool()));
    assertEquals(Bool(), Bool().rel().equalsTo(Bool()));
  }
}
