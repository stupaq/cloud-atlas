package stupaq.cloudatlas.attribute.types;

import org.junit.Test;

import stupaq.cloudatlas.interpreter.errors.TypeCheckerException;
import stupaq.cloudatlas.interpreter.typecheck.TypeInfo;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static stupaq.cloudatlas.attribute.types.AttributeTypeTestUtils.*;
import static stupaq.cloudatlas.interpreter.TypeInfoTestUtils.TBool;
import static stupaq.cloudatlas.interpreter.TypeInfoTestUtils.TInt;
import static stupaq.cloudatlas.interpreter.TypeInfoTestUtils.TList;
import static stupaq.cloudatlas.interpreter.TypeInfoTestUtils.TStr;

public class CAListTest {
  @Test
  public void testAuxiliary() {
    assertEquals(List(TList(TInt())), List(TList(TInt())));
    assertFalse(List(TList(TInt())).equals(List(TList(TInt()))));
  }

  @Test
  public void testUniformity() {
    List(TStr(), Str("string1"), Str("string2"));
  }

  @Test(expected = TypeCheckerException.class)
  public void testNonuniformity() {
    List((TypeInfo) TStr(), Str("string1"), Int(1337L));
  }

  @Test
  public void testConversions() {
    // -> CAString
    assertEquals(Str("[  ]"), List(TStr()).to().String());
    assertEquals(Str("[ aaa, bb ]"), List(TStr(), Str("aaa"), Str("bb")).to().String());
    assertEquals(Str("[ [ 337 ], [ 1337 ] ]"),
        List(TList(TInt()), List(TInt(), Int(337L)), List(TInt(), Int(1337L))).to().String());
    // -> CASet
    assertEquals(Set(TInt()), List(TInt()).to().Set());
    assertEquals(Set(TStr(), Str("aaa"), Str("bb")),
        List(TStr(), Str("aaa"), Str("bb")).to().Set());
    assertEquals(Set(TStr(), Str("aaa")), List(TStr(), Str("aaa"), Str("aaa")).to().Set());
    // We have to resolve ambiguity here and tell Java to treat internal CAList as a single
    // attribute instead of collection of attributes.
    assertEquals(Set(TList(TInt()), List(TInt(), Int(1337L))),
        List(TList(TInt()), List(TInt(), Int(1337L)), List(TInt(), Int(1337L))).to().Set());
  }

  @Test
  public void testOperations() {
    assertEquals(Int(0L), List(TInt()).op().size());
    assertEquals(Int(3L), List(TBool(), Bool(true), Bool(false), Bool(true)).op().size());
  }

  @Test
  public void testRelational() {
    assertEquals(Bool(true),
        List(TInt(), Int(2), Int(3)).rel().equalsTo(List(TInt(), Int(2), Int(3))));
    assertEquals(Bool(false),
        List(TInt(), Int(3), Int(2)).rel().equalsTo(List(TInt(), Int(2), Int(3))));
  }
}
