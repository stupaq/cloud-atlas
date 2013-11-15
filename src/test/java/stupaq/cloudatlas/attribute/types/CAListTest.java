package stupaq.cloudatlas.attribute.types;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static stupaq.cloudatlas.attribute.types.AttributeTypeTestUtils.*;

public class CAListTest {
  @Test
  public void testUniformity() {
    List(Str("string1"), Str("string2"));
  }

  @Test(expected = IllegalStateException.class)
  public void testNonuniformity() {
    List(Str("string1"), Int(1337L));
  }

  @Test
  public void testConversions() {
    // -> CAString
    assertEquals(Str("[  ]"), List().to().String());
    assertEquals(Str("[ aaa, bb ]"), List(Str("aaa"), Str("bb")).to().String());
    assertEquals(Str("[ [ 337 ], [ 1337 ] ]"),
        List(List(Int(337L)), List(Int(1337L))).to().String());
    // -> CASet
    assertEquals(Set(), List().to().Set());
    assertEquals(Set(Str("aaa"), Str("bb")), List(Str("aaa"), Str("bb")).to().Set());
    assertEquals(Set(Str("aaa")), List(Str("aaa"), Str("aaa")).to().Set());
    // We have to resolve ambiguity here and tell Java to treat internal CAList as a single
    // attribute instead of collection of attributes.
    assertEquals(Set(List(Int(1337L))), List(List(Int(1337L)), List(Int(1337L))).to().Set());
  }

  @Test
  public void testOperations() {
    assertEquals(Int(0L), List().op().size());
    assertEquals(Int(3L), List(Bool(true), Bool(false), Bool(true)).op().size());
  }

  @Test
  public void testRelational() {
    assertEquals(Bool(true), List(Int(2), Int(3)).rel().equalsTo(List(Int(2), Int(3))));
    assertEquals(Bool(false), List(Int(3), Int(2)).rel().equalsTo(List(Int(2), Int(3))));
  }
}
