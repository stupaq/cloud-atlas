package stupaq.cloudatlas.attribute.types;

import org.junit.Test;

import stupaq.cloudatlas.interpreter.semantics.OperableValue.OperableValueDefault;
import stupaq.cloudatlas.interpreter.semantics.RelationalValue.RelationalValueDefault;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static stupaq.cloudatlas.attribute.types.AttributeValueTestUtils.Cont;
import static stupaq.cloudatlas.attribute.types.AttributeValueTestUtils.Str;

public class CAContactTest {
  @Test
  public void testConversions() {
    // -> CAString
    assertEquals(Str("UW1"), Cont("UW1").to().String());
    assertEquals(Str("UW2"), Cont("UW2").to().String());
    assertEquals(Str(), Cont().to().String());
  }

  @Test
  public void testOperations() {
    assertTrue(Cont("UW1").op() instanceof OperableValueDefault);
  }

  @Test
  public void testRelational() {
    assertTrue(Cont("UW1").rel() instanceof RelationalValueDefault);
  }
}
