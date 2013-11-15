package stupaq.cloudatlas.attribute.types;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static stupaq.cloudatlas.attribute.types.AttributeTypeTestUtils.Bool;
import static stupaq.cloudatlas.attribute.types.AttributeTypeTestUtils.Dur;
import static stupaq.cloudatlas.attribute.types.AttributeTypeTestUtils.Str;
import static stupaq.cloudatlas.attribute.types.AttributeTypeTestUtils.Time;

public class CATimeTest {
  @Test
  public void testConversions() {
    // -> CATime
    assertEquals(Str("2000/01/01 00:00:00.000 CET"), CATime.epoch().to().String());
    // CATime#now()
    System.err.println("CATime.now() returned: " + CATime.now().to().String());
  }

  @Test
  public void testOperations() {
    // negation
    assertEquals(Time(-12345L), Time(12345L).op().negate());
    // addition
    assertEquals(Dur(-1000L), Time(10000L).op().add(Time(11000L).op().negate()));
  }

  @Test
  public void testRelational() {
    assertEquals(Bool(true), Time(1).rel().equalsTo(Time(1)));
    assertEquals(Bool(false), Time(2).rel().equalsTo(Time(1)));
    assertEquals(Bool(false), Time(1).rel().greaterThan(Time(1)));
    assertEquals(Bool(true), Time(2).rel().greaterThan(Time(1)));
  }
}
