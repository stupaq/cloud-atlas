package stupaq.cloudatlas.attribute.types;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class CATupleTest {
  @Test
  public void testUniformity() {
    new CATuple(new CAString("string1"), new CAString("string2"));
    new CATuple(new CAString("string1"), new CAInteger(1337L));
  }

  @Test
  public void testConversions() {
    // -> CAString
    assertEquals(new CAString("<<  >>"), new CATuple().getConvertible().to_String());
    assertEquals(new CAString("<< aaa, 1337 >>"),
        new CATuple(new CAString("aaa"), new CAInteger(1337L)).getConvertible().to_String());
    assertEquals(new CAString("<< << aaa >>, << 1337 >> >>"),
        new CATuple(new CATuple(new CAString("aaa")), new CATuple(new CAInteger(1337L)))
            .getConvertible().to_String());
  }
}
