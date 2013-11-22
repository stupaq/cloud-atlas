package stupaq.cloudatlas.attribute.values;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static stupaq.cloudatlas.attribute.values.AttributeValueTestUtils.*;
import static stupaq.cloudatlas.attribute.types.TypeInfoTestUtils.TStr;

public class CAStringTest {
  @Test
  public void testAuxiliary() {
    assertNotEquals(Str(), Bool());
    assertNotEquals(Str(), Cont());
    assertNotEquals(Str(), Doub());
    assertNotEquals(Str(), Dur());
    assertNotEquals(Str(), Int());
    assertNotEquals(Str(), List(TStr()));
    assertNotEquals(Str(), Set(TStr()));
    assertNotEquals(Str(), Time());
  }

  @Test
  public void testConversions() {
    // -> CABoolean
    assertEquals(Bool(true), Str("true").to().Boolean());
    assertEquals(Bool(false), Str("false").to().Boolean());
    assertEquals(Bool(), Str().to().Boolean());
    // -> CAInteger
    assertEquals(Int(1337L), Str("1337").to().Integer());
    assertEquals(Int(), Str().to().Integer());
    // -> CADouble
    assertEquals(Doub(1337.33D), Str("1337.33").to().Double());
    assertEquals(Doub(), Str().to().Double());
    // -> CAContact
    assertEquals(Cont("UW1"), Str("UW1").to().Contact());
    assertEquals(Cont(), Str().to().Contact());
    // -> CAString
    assertEquals(Str("UW1"), Str("UW1").to().String());
    assertEquals(Str(), Str().to().String());
    // -> CATime
    assertEquals(CATime.epoch(), Str("2000/01/01 00:00:00.000 CET").to().Time());
    assertEquals(Time(), Str().to().Time());
    // -> CADuration
    assertEquals(Dur(189141024L), Str("+2 04:32:21.024").to().Duration());
    assertEquals(Dur(-189141024L), Str("-2 04:32:21.024").to().Duration());
    assertEquals(Dur(6235522024L), Str("+72 04:05:22.024").to().Duration());
    assertEquals(Dur(-6235522024L), Str("-72 04:05:22.024").to().Duration());
    assertEquals(Dur(), Str().to().Duration());
  }

  @Test
  public void testOperations() {
    // addition
    assertEquals(Str("aaaabbb"), Str("aaaa").op().add(Str("bbb")));
    assertEquals(Str("aaaa"), Str("aaaa").op().add(Str("")));
    assertEquals(Str("aaaa"), Str("aaaa").op().add(Str("")));
    assertEquals(Str(), Str().op().add(Str("")));
    assertEquals(Str(), Str().op().add(Str()));
    assertEquals(Str(), Str("aaaa").op().add(Str()));
    // size
    assertEquals(Int(3L), Str("aaa").op().size());
    assertEquals(Int(0L), Str("").op().size());
    assertEquals(Int(), Str().op().size());
    // regexp
    assertEquals(Bool(true), Str("aaaaaab").op().matches(Str("a*b")));
    assertEquals(Bool(false), Str("aaaacb").op().matches(Str("a*b")));
    assertEquals(Bool(), Str().op().matches(Str("a*b")));
    assertEquals(Bool(), Str().op().matches(Str()));
    assertEquals(Bool(), Str("aaa").op().matches(Str()));
  }

  @Test
  public void testRelational() {
    assertEquals(Bool(true), Str("1").rel().equalsTo(Str("1")));
    assertEquals(Bool(false), Str("2").rel().equalsTo(Str("1")));
    assertEquals(Bool(false), Str("1").rel().greaterThan(Str("1")));
    assertEquals(Bool(true), Str("2").rel().greaterThan(Str("1")));
    assertEquals(Bool(), Str("2").rel().greaterThan(Str()));
    assertEquals(Bool(), Str().rel().greaterThan(Str("1")));
    assertEquals(Bool(), Str().rel().greaterThan(Str()));
  }
}
