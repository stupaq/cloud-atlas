package stupaq.cloudatlas.attribute.types;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class CASetTest {
  @Test
  public void testUniformity() {
    new CASet<>(new CAString("string1"), new CAString("string2"));
  }

  @Test(expected = IllegalStateException.class)
  public void testNonuniformity() {
    new CASet<>(new CAString("string1"), new CAInteger(1337L));
  }

  @Test
  public void testConversions() {
    // -> CAString
    assertEquals(new CAString("{  }"), new CASet<>().to().String());
    assertEquals(new CAString("{ aaa, bb }"),
        new CASet<>(new CAString("aaa"), new CAString("bb")).to().String());
    assertEquals(new CAString("{ { 1337 }, { aaa } }"),
        new CASet<>(new CASet<>(new CAString("aaa")), new CASet<>(new CAInteger(1337L))).to()
            .String());
    // -> CAList
    assertEquals(new CAList<>(), new CASet<>().to().List());
    assertEquals(new CAList<>(new CAString("aaa"), new CAString("bb")),
        new CASet<>(new CAString("aaa"), new CAString("bb")).to().List());
    assertEquals(new CAList<>(new CAString("aaa")), new CASet<>(new CAString("aaa")).to().List());
    assertEquals(new CAList<>(new CAList<>(new CAInteger(337L))),
        new CASet<>(new CAList<>(new CAInteger(337L))).to().List());
  }

  @Test
  public void testOperations() {
    assertEquals(new CAInteger(0L), new CASet<>().op().size());
    assertEquals(new CAInteger(2L),
        new CASet<>(new CABoolean(true), new CABoolean(false), new CABoolean(true)).op().size());
  }

  @Test
  public void testRelational() {
    assertEquals(new CABoolean(true), new CASet<>(new CAInteger(2), new CAInteger(3)).rel()
        .equalsTo(new CASet<>(new CAInteger(3), new CAInteger(2))));
    assertEquals(new CABoolean(false), new CASet<>(new CAInteger(1), new CAInteger(2)).rel()
        .equalsTo(new CASet<>(new CAInteger(2), new CAInteger(3))));
  }
}
