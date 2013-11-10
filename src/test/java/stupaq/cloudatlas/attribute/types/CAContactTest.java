package stupaq.cloudatlas.attribute.types;

import org.junit.Test;

import stupaq.cloudatlas.interpreter.semantics.OperableValue.OperableValueDefault;
import stupaq.cloudatlas.interpreter.semantics.RelationalValue.RelationalValueDefault;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class CAContactTest {
  @Test
  public void testConversions() {
    // -> CAString
    assertEquals(new CAString("UW1"), new CAContact("UW1").to().String());
    assertEquals(new CAString("UW2"), new CAContact("UW2").to().String());
  }

  @Test
  public void testOperations() {
    assertTrue(new CAContact("UW1").op() instanceof OperableValueDefault);
  }

  @Test
  public void testRelational() {
    assertTrue(new CAContact("UW1").rel() instanceof RelationalValueDefault);
  }
}
