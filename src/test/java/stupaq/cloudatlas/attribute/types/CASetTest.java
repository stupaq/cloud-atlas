package stupaq.cloudatlas.attribute.types;

import org.junit.Test;

public class CASetTest {
  @Test
  public void testUniformity() {
    new CASet<>(new CAString("string1"), new CAString("string2"));
  }

  @Test(expected = IllegalStateException.class)
  public void testNonuniformity() {
    new CASet<>(new CAString("string1"), new CAInteger(1337L));
  }
}
