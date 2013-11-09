package stupaq.cloudatlas.attribute.types;

import org.junit.Test;

public class CAListTest {
  @Test
  public void testUniformity() {
    new CAList<>(new CAString("string1"), new CAString("string2"));
  }

  @Test(expected = IllegalStateException.class)
  public void testNonuniformity() {
    new CAList<>(new CAString("string1"), new CAInteger(1337L));
  }
}
