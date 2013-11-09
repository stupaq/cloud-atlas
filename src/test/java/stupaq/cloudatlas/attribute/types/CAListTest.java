package stupaq.cloudatlas.attribute.types;

import org.junit.Test;

import java.util.Collections;

import static org.junit.Assert.assertEquals;

public class CAListTest {
  @Test
  public void testUniformity() {
    new CAList<>(new CAString("string1"), new CAString("string2"));
  }

  @Test(expected = IllegalStateException.class)
  public void testNonuniformity() {
    new CAList<>(new CAString("string1"), new CAInteger(1337L));
  }

  @Test
  public void testConversions() {
    // -> CAString
    assertEquals(new CAString("[  ]"), new CAList<>().getConvertible().to_String());
    assertEquals(new CAString("[ aaa, bb ]"),
        new CAList<>(new CAString("aaa"), new CAString("bb")).getConvertible().to_String());
    assertEquals(new CAString("[ [ 337 ], [ 1337 ] ]"),
        new CAList<>(new CAList<>(new CAInteger(337L)), new CAList<>(new CAInteger(1337L)))
            .getConvertible().to_String());
    // -> CASet
    assertEquals(new CASet<>(), new CAList<>().getConvertible().to_Set());
    assertEquals(new CASet<>(new CAString("aaa"), new CAString("bb")),
        new CAList<>(new CAString("aaa"), new CAString("bb")).getConvertible().to_Set());
    assertEquals(new CASet<>(new CAString("aaa")),
        new CAList<>(new CAString("aaa"), new CAString("aaa")).getConvertible().to_Set());
    // We have to resolve ambiguity here and tell Java to treat internal CAList as a single
    // attribute instead of collection of attributes.
    assertEquals(new CASet<>(Collections.singleton(new CAList<>(new CAInteger(1337L)))),
        new CAList<>(new CAList<>(new CAInteger(1337L)), new CAList<>(new CAInteger(1337L)))
            .getConvertible().to_Set());
  }
}
