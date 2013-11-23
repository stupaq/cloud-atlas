package stupaq.cloudatlas.attribute.values;

import org.junit.Test;

import stupaq.cloudatlas.query.errors.TypeCheckerException;
import stupaq.cloudatlas.query.typecheck.TypeInfo;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static stupaq.cloudatlas.attribute.values.AttributeValueTestUtils.*;
import static stupaq.cloudatlas.query.typecheck.TypeInfoTestUtils.*;

public class CASetTest {
  @Test
  public void testAuxiliary() {
    assertEquals(Set(TSet(TInt())), Set(TSet(TInt())));
    assertFalse(Set(TSet(TInt())).equals(Set(TSet(TDoub()))));
  }

  @Test
  public void testUniformity() {
    Set(TStr(), Str("string1"), Str("string2"));
  }

  @Test(expected = TypeCheckerException.class)
  public void testNonuniformity() {
    Set((TypeInfo) TStr(), Str("string1"), Int(1337L));
  }

  @Test
  public void testConversions() {
    // -> CAString
    assertEquals(Str("{  }"), Set(TStr()).to().String());
    assertEquals(Str("{ aaa, bb }"), Set(TStr(), Str("aaa"), Str("bb")).to().String());
    assertEquals(Str("{ { 1337 }, { aaa } }"),
        Set(TSet(TStr()), Set(TStr(), Str("1337")), Set(TStr(), Str("aaa"))).to().String());
    assertEquals(Str(), SetNull(TStr()).to().String());
    // -> CAList
    assertEquals(List(TInt()), Set(TInt()).to().List());
    assertEquals(List(TStr(), Str("aaa"), Str("bb")),
        Set(TStr(), Str("aaa"), Str("bb")).to().List());
    assertEquals(List(TStr(), Str("aaa")), Set(TStr(), Str("aaa")).to().List());
    assertEquals(List(TList(TInt()), List(TInt(), Int(337L))),
        Set(TList(TInt()), List(TInt(), Int(337L))).to().List());
    assertEquals(ListNull(TList(TInt())), SetNull(TList(TInt())).to().List());
  }

  @Test
  public void testOperations() {
    assertEquals(Int(0L), Set(TInt()).op().size());
    assertEquals(Int(2L), Set(TBool(), Bool(true), Bool(false), Bool(true)).op().size());
    assertEquals(Int(), SetNull(TInt()).op().size());
  }

  @Test
  public void testRelational() {
    assertEquals(Bool(true),
        Set(TInt(), Int(2), Int(3)).rel().equalsTo(Set(TInt(), Int(3), Int(2))));
    assertEquals(Bool(false),
        Set(TInt(), Int(1), Int(2)).rel().equalsTo(Set(TInt(), Int(2), Int(3))));
    assertEquals(Bool(), SetNull(TInt()).rel().equalsTo(Set(TInt(), Int(2))));
    assertEquals(Bool(), Set(TInt(), Int(2)).rel().equalsTo(SetNull(TInt())));
    assertEquals(Bool(), SetNull(TInt()).rel().equalsTo(SetNull(TInt())));
  }
}
