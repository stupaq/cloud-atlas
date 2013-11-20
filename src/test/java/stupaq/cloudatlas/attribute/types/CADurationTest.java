package stupaq.cloudatlas.attribute.types;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static stupaq.cloudatlas.attribute.types.AttributeValueTestUtils.*;

public class CADurationTest {
  @Test
  public void testConversions() {
    // -> CAInteger
    assertEquals(Int(1337L), Dur(1337L).to().Integer());
    assertEquals(Int(), Dur().to().Integer());
    // -> CAString
    assertEquals(Str("+2 04:32:21.024"), Dur(189141024L).to().String());
    assertEquals(Str("-2 04:32:21.024"), Dur(-189141024L).to().String());
    assertEquals(Str("+72 04:05:22.024"), Dur(6235522024L).to().String());
    assertEquals(Str("-72 04:05:22.024"), Dur(-6235522024L).to().String());
    assertEquals(Str(), Dur().to().String());
  }

  @Test
  public void testOperations() {
    // negation
    assertEquals(Dur(1000L), Dur(-1000L).op().negate());
    assertEquals(Dur(), Dur().op().negate());
    // addition
    assertEquals(Time(1000L), Time(900L).op().add(Dur(100L)));
    assertEquals(Time(1000L), Time(100L).op().add(Dur(900L)));
    assertEquals(Time(1000L), Dur(900L).op().add(Time(100L)));
    assertEquals(Time(1000L), Dur(100L).op().add(Time(900L)));
    assertEquals(Dur(1000L), Dur(900L).op().add(Dur(100L)));
    assertEquals(Dur(1000L), Dur(100L).op().add(Dur(900L)));
    assertEquals(Dur(), Dur().op().add(Dur(900L)));
    assertEquals(Dur(), Dur(100L).op().add(Dur()));
    assertEquals(Dur(), Dur().op().add(Dur()));
    // multiplication
    assertEquals(Dur(1000L), Dur(100L).op().multiply(Int(10L)));
    assertEquals(Dur(1000L), Dur(100L).op().multiply(Doub(10L)));
    assertEquals(Dur(1000L), Doub(100L).op().multiply(Dur(10L)));
    assertEquals(Dur(1000L), Int(100L).op().multiply(Dur(10L)));
    assertEquals(Dur(), Int().op().multiply(Dur(10L)));
    assertEquals(Dur(), Int(100L).op().multiply(Dur()));
    assertEquals(Dur(), Int().op().multiply(Dur()));
  }

  @Test
  public void testRelational() {
    assertEquals(Bool(true), Dur(1).rel().equalsTo(Dur(1)));
    assertEquals(Bool(false), Dur(2).rel().equalsTo(Dur(1)));
    assertEquals(Bool(false), Dur(1).rel().greaterThan(Dur(1)));
    assertEquals(Bool(true), Dur(2).rel().greaterThan(Dur(1)));
    assertEquals(Bool(), Dur().rel().greaterThan(Dur(1)));
    assertEquals(Bool(), Dur(2).rel().greaterThan(Dur()));
    assertEquals(Bool(), Dur().rel().greaterThan(Dur()));
  }
}
