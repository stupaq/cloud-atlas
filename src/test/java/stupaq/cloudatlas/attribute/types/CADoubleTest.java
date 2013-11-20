package stupaq.cloudatlas.attribute.types;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static stupaq.cloudatlas.attribute.types.AttributeValueTestUtils.Bool;
import static stupaq.cloudatlas.attribute.types.AttributeValueTestUtils.Doub;
import static stupaq.cloudatlas.attribute.types.AttributeValueTestUtils.Int;
import static stupaq.cloudatlas.attribute.types.AttributeValueTestUtils.Str;

public class CADoubleTest {
  @Test
  public void testConversions() {
    // -> CAString
    assertEquals(Str("0.123"), Doub(0.123D).to().String());
    assertEquals(Str(), Doub().to().String());
    // -> CAInteger
    assertEquals(Int(1L), Doub(1.99999D).to().Integer());
    assertEquals(Int(), Doub().to().Integer());
  }

  @Test
  public void testOperations() {
    // zero
    assertEquals(Doub(0), Doub().op().zero());
    // addition
    assertEquals(Doub(3.0), Doub(2.0).op().add(Doub(1.0)));
    assertEquals(Doub(3.0), Int(2L).op().add(Doub(1.0)));
    assertEquals(Doub(3.0), Doub(2.0).op().add(Int(1L)));
    assertEquals(Doub(), Doub().op().add(Int()));
    assertEquals(Doub(), Doub(2.0).op().add(Int()));
    assertEquals(Doub(), Doub().op().add(Int(1L)));
    // negation
    assertEquals(Doub(-3.0), Doub(3.0).op().negate());
    assertEquals(Doub(), Doub().op().negate());
    // multiplication
    assertEquals(Doub(6.0), Doub(2.0).op().multiply(Doub(3.0)));
    assertEquals(Doub(6.0), Int(2L).op().multiply(Doub(3.0)));
    assertEquals(Doub(6.0), Doub(2.0).op().multiply(Int(3L)));
    assertEquals(Doub(), Doub().op().multiply(Int(3L)));
    assertEquals(Doub(), Doub(2.0).op().multiply(Int()));
    assertEquals(Doub(), Doub().op().multiply(Int()));
    // inversion
    assertEquals(Doub(1.0 / 3.0), Doub(3.0).op().inverse());
    assertEquals(Doub(), Doub().op().inverse());
    // round
    assertEquals(Doub(1.0), Doub(0.5).op().round());
    assertEquals(Doub(1.0), Doub(0.6).op().round());
    assertEquals(Doub(0.0), Doub(0.4).op().round());
    assertEquals(Doub(), Doub().op().round());
    // ceil
    assertEquals(Doub(1.0), Doub(0.5).op().ceil());
    assertEquals(Doub(1.0), Doub(0.6).op().ceil());
    assertEquals(Doub(1.0), Doub(0.4).op().ceil());
    assertEquals(Doub(0.0), Doub(0.0).op().ceil());
    assertEquals(Doub(), Doub().op().ceil());
    // floor
    assertEquals(Doub(0.0), Doub(0.5).op().floor());
    assertEquals(Doub(0.0), Doub(0.6).op().floor());
    assertEquals(Doub(0.0), Doub(0.4).op().floor());
    assertEquals(Doub(1.0), Doub(1.0).op().floor());
    assertEquals(Doub(), Doub().op().floor());
  }

  @Test
  public void testRelational() {
    assertEquals(Bool(true), Doub(1).rel().equalsTo(Doub(1)));
    assertEquals(Bool(false), Doub(2).rel().equalsTo(Doub(1)));
    assertEquals(Bool(false), Doub(1).rel().greaterThan(Doub(1)));
    assertEquals(Bool(true), Doub(2).rel().greaterThan(Doub(1)));
    assertEquals(Bool(), Doub().rel().greaterThan(Doub(1)));
    assertEquals(Bool(), Doub(2).rel().greaterThan(Doub()));
    assertEquals(Bool(), Doub().rel().greaterThan(Doub()));
  }
}
