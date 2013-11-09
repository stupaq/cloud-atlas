package stupaq.cloudatlas.attribute.types;

import org.junit.Test;

public class CATupleTest {
  @Test
  public void testUniformity() {
    new CATuple(new CAString("string1"), new CAString("string2"));
    new CATuple(new CAString("string1"), new CAInteger(1337L));
  }
}
