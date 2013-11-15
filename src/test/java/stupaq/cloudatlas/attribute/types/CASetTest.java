package stupaq.cloudatlas.attribute.types;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static stupaq.cloudatlas.attribute.types.AttributeTypeTestUtils.*;

public class CASetTest {
  @Test
  public void testUniformity() {
    Set(Str("string1"), Str("string2"));
  }

  @Test(expected = IllegalStateException.class)
  public void testNonuniformity() {
    Set(Str("string1"), Int(1337L));
  }

  @Test
  public void testConversions() {
    // -> CAString
    assertEquals(Str("{  }"), Set().to().String());
    assertEquals(Str("{ aaa, bb }"), Set(Str("aaa"), Str("bb")).to().String());
    assertEquals(Str("{ { 1337 }, { aaa } }"), Set(Set(Str("aaa")), Set(Int(1337L))).to().String());
    // -> CAList
    assertEquals(List(), Set().to().List());
    assertEquals(List(Str("aaa"), Str("bb")), Set(Str("aaa"), Str("bb")).to().List());
    assertEquals(List(Str("aaa")), Set(Str("aaa")).to().List());
    assertEquals(List(List(Int(337L))), Set(List(Int(337L))).to().List());
  }

  @Test
  public void testOperations() {
    assertEquals(Int(0L), Set().op().size());
    assertEquals(Int(2L), Set(Bool(true), Bool(false), Bool(true)).op().size());
  }

  @Test
  public void testRelational() {
    assertEquals(Bool(true), Set(Int(2), Int(3)).rel().equalsTo(Set(Int(3), Int(2))));
    assertEquals(Bool(false), Set(Int(1), Int(2)).rel().equalsTo(Set(Int(2), Int(3))));
  }
}
