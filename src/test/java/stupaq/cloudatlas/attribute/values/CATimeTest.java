package stupaq.cloudatlas.attribute.values;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static stupaq.cloudatlas.attribute.values.AttributeValueTestUtils.Bool;
import static stupaq.cloudatlas.attribute.values.AttributeValueTestUtils.Dur;
import static stupaq.cloudatlas.attribute.values.AttributeValueTestUtils.Str;
import static stupaq.cloudatlas.attribute.values.AttributeValueTestUtils.Time;

public class CATimeTest {
  @Test
  public void testTimeBase() {
    assertEquals(Time(0), Str("1970/01/01 00:00:00.000 GMT").to().Time());
  }

  @Test
  public void testConversions() {
    // -> CATime
    assertEquals(Str("2000/01/01 00:00:00.000 CET"), CATime.epoch().to().String());
    assertEquals(Str(), Time().to().String());
    // CATime#now()
    System.err.println("CATime.now() returned: " + CATime.now().to().String());
  }

  @Test
  public void testOperations() {
    // negation
    assertEquals(Time(-12345L), Time(12345L).op().negate());
    assertEquals(Time(), Time().op().negate());
    // addition
    assertEquals(Dur(-1000L), Time(10000L).op().add(Time(11000L).op().negate()));
    assertEquals(Dur(), Time().op().add(Time(11000L).op().negate()));
    assertEquals(Dur(), Time(10000L).op().add(Time().op().negate()));
    assertEquals(Dur(), Time().op().add(Time().op().negate()));
  }

  @Test
  public void testRelational() {
    assertEquals(Bool(true), Time(1).rel().equalsTo(Time(1)));
    assertEquals(Bool(false), Time(2).rel().equalsTo(Time(1)));
    assertEquals(Bool(), Time(2).rel().equalsTo(Time()));
    assertEquals(Bool(), Time().rel().equalsTo(Time(1)));
    assertEquals(Bool(), Time().rel().equalsTo(Time()));
    assertEquals(Bool(false), Time(1).rel().greaterThan(Time(1)));
    assertEquals(Bool(true), Time(2).rel().greaterThan(Time(1)));
    assertEquals(Bool(), Time(2).rel().greaterThan(Time()));
    assertEquals(Bool(), Time().rel().greaterThan(Time(1)));
    assertEquals(Bool(), Time().rel().greaterThan(Time()));
  }
}
