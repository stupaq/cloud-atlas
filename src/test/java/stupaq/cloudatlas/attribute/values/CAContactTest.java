package stupaq.cloudatlas.attribute.values;

import org.junit.Test;

import stupaq.cloudatlas.query.semantics.OperableValue.OperableValueDefault;
import stupaq.cloudatlas.query.semantics.RelationalValue.RelationalValueDefault;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static stupaq.cloudatlas.attribute.values.AttributeValueTestUtils.Cont;
import static stupaq.cloudatlas.attribute.values.AttributeValueTestUtils.Str;

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
