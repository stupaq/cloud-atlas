package stupaq.cloudatlas.attribute.types;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static stupaq.cloudatlas.attribute.types.AttributeTypeTestUtils.Bool;
import static stupaq.cloudatlas.attribute.types.AttributeTypeTestUtils.Doub;
import static stupaq.cloudatlas.attribute.types.AttributeTypeTestUtils.Int;
import static stupaq.cloudatlas.attribute.types.AttributeTypeTestUtils.Str;

public class CADoubleTest {
  @Test
  public void testConversions() {
    // -> CAString
    assertEquals(Str("0.123"), Doub(0.123D).to().String());
    // -> CAInteger
    assertEquals(Int(1L), Doub(1.99999D).to().Integer());
  }

  @Test
  public void testOperations() {
    // addition
    assertEquals(Doub(3.0), Doub(2.0).op().add(Doub(1.0)));
    assertEquals(Doub(3.0), Int(2L).op().add(Doub(1.0)));
    assertEquals(Doub(3.0), Doub(2.0).op().add(Int(1L)));
    // negation
    assertEquals(Doub(-3.0), Doub(3.0).op().negate());
    // multiplication
    assertEquals(Doub(6.0), Doub(2.0).op().multiply(Doub(3.0)));
    assertEquals(Doub(6.0), Int(2L).op().multiply(Doub(3.0)));
    assertEquals(Doub(6.0), Doub(2.0).op().multiply(Int(3L)));
    // inversion
    assertEquals(Doub(1.0 / 3.0), Doub(3.0).op().inverse());
    // round
    assertEquals(Doub(1.0), Doub(0.5).op().round());
    assertEquals(Doub(1.0), Doub(0.6).op().round());
    assertEquals(Doub(0.0), Doub(0.4).op().round());
    // ceil
    assertEquals(Doub(1.0), Doub(0.5).op().ceil());
    assertEquals(Doub(1.0), Doub(0.6).op().ceil());
    assertEquals(Doub(1.0), Doub(0.4).op().ceil());
    assertEquals(Doub(0.0), Doub(0.0).op().ceil());
    // floor
    assertEquals(Doub(0.0), Doub(0.5).op().floor());
    assertEquals(Doub(0.0), Doub(0.6).op().floor());
    assertEquals(Doub(0.0), Doub(0.4).op().floor());
    assertEquals(Doub(1.0), Doub(1.0).op().floor());
  }

  @Test
  public void testRelational() {
    assertEquals(Bool(true), Doub(1).rel().equalsTo(Doub(1)));
    assertEquals(Bool(false), Doub(2).rel().equalsTo(Doub(1)));
    assertEquals(Bool(false), Doub(1).rel().greaterThan(Doub(1)));
    assertEquals(Bool(true), Doub(2).rel().greaterThan(Doub(1)));
  }
}
