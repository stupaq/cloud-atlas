package stupaq.cloudatlas.attribute.types;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static stupaq.cloudatlas.attribute.types.AttributeTypeTestUtils.*;

public class CAStringTest {
  @Test
  public void testConversions() {
    // -> CABoolean
    assertEquals(Bool(true), Str("true").to().Boolean());
    assertEquals(Bool(false), Str("false").to().Boolean());
    // -> CAInteger
    assertEquals(Int(1337L), Str("1337").to().Integer());
    // -> CADouble
    assertEquals(Doub(1337.33D), Str("1337.33").to().Double());
    // -> CAContact
    assertEquals(Cont("UW1"), Str("UW1").to().Contact());
    // -> CAString
    assertEquals(Str("UW1"), Str("UW1").to().String());
    // -> CATime
    assertEquals(CATime.epoch(), Str("2000/01/01 00:00:00.000 CET").to().Time());
    // -> CADuration
    assertEquals(Dur(189141024L), Str("+2 04:32:21.024").to().Duration());
    assertEquals(Dur(-189141024L), Str("-2 04:32:21.024").to().Duration());
    assertEquals(Dur(6235522024L), Str("+72 04:05:22.024").to().Duration());
    assertEquals(Dur(-6235522024L), Str("-72 04:05:22.024").to().Duration());
  }

  @Test
  public void testOperations() {
    // addition
    assertEquals(Str("aaaabbb"), Str("aaaa").op().add(Str("bbb")));
    assertEquals(Str("aaaa"), Str("aaaa").op().add(Str("")));
    // size
    assertEquals(Int(3L), Str("aaa").op().size());
    assertEquals(Int(0L), Str("").op().size());
    // regexp
    assertEquals(Bool(true), Str("aaaaaab").op().matches(Str("a*b")));
    assertEquals(Bool(false), Str("aaaacb").op().matches(Str("a*b")));
  }

  @Test
  public void testRelational() {
    assertEquals(Bool(true), Str("1").rel().equalsTo(Str("1")));
    assertEquals(Bool(false), Str("2").rel().equalsTo(Str("1")));
    assertEquals(Bool(false), Str("1").rel().greaterThan(Str("1")));
    assertEquals(Bool(true), Str("2").rel().greaterThan(Str("1")));
  }
}
