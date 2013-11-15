package stupaq.cloudatlas.attribute.types;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static stupaq.cloudatlas.attribute.types.AttributeTypeTestUtils.*;

public class CAIntegerTest {
  @Test
  public void testConversions() {
    // -> CAString
    assertEquals(Str("1337"), Int(1337L).to().String());
    // -> CADouble
    assertEquals(Doub(1337.0D), Int(1337L).to().Double());
    // -> CADuration
    assertEquals(Dur(1337L), Int(1337L).to().Duration());
  }

  @Test
  public void testOperations() {
    // addition
    assertEquals(Int(3L), Int(2L).op().add(Int(1L)));
    assertEquals(Doub(3.0), Int(2L).op().add(Doub(1.0)));
    assertEquals(Doub(3.0), Doub(2.0).op().add(Int(1L)));
    // negation
    assertEquals(Int(-3L), Int(3L).op().negate());
    // multiplication
    assertEquals(Int(6L), Int(2L).op().multiply(Int(3L)));
    assertEquals(Doub(6.0), Int(2L).op().multiply(Doub(3.0)));
    assertEquals(Doub(6.0), Doub(2.0).op().multiply(Int(3L)));
    // inversion
    assertEquals(Doub(1.0 / 3.0), Int(3L).op().inverse());
    // modulo
    assertEquals(Int(2L), Int(5L).op().modulo(Int(3L)));
    assertEquals(Int(1L), Int(4L).op().modulo(Int(3L)));
    assertEquals(Int(0L), Int(3L).op().modulo(Int(3L)));
  }

  @Test
  public void testRelational() {
    assertEquals(Bool(true), Int(1).rel().equalsTo(Int(1)));
    assertEquals(Bool(false), Int(2).rel().equalsTo(Int(1)));
    assertEquals(Bool(false), Int(1).rel().greaterThan(Int(1)));
    assertEquals(Bool(true), Int(2).rel().greaterThan(Int(1)));
  }
}
