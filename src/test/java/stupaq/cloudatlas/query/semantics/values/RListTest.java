package stupaq.cloudatlas.query.semantics.values;

import org.junit.Test;

import stupaq.cloudatlas.query.errors.TypeCheckerException;
import stupaq.cloudatlas.query.semantics.AggregatingValue.AggregatingValueDefault;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static stupaq.cloudatlas.attribute.values.AttributeValueTestUtils.Bool;
import static stupaq.cloudatlas.attribute.values.AttributeValueTestUtils.Int;
import static stupaq.cloudatlas.query.semantics.values.SemanticValueTestUtils.*;
import static stupaq.cloudatlas.query.typecheck.TypeInfoTestUtils.TBool;
import static stupaq.cloudatlas.query.typecheck.TypeInfoTestUtils.TInt;

public class RListTest {
  @Test
  public void testSemanticsNull() {
    assertEquals(L(TBool(), Bool(true), Bool(false), Bool(false)),
        L(TInt(), Int(), Int(2), Int(3)).isNull());
    assertEquals(L(TInt()), L(TInt()).isNull());
  }

  @Test
  public void testSemanticsMap() {
    // map
    assertEquals(L(TInt(), Int(-2), Int(-3)), L(TInt(), Int(2), Int(3)).map(UnOp()));
    // map null
    assertEquals(L(TInt(), Int(), Int(-3)), L(TInt(), Int(), Int(3)).map(UnOp()));
  }

  @Test(expected = TypeCheckerException.class)
  public void testSemanticsZip0() {
    // zip
    assertEquals(L(TInt(), Int(3), Int(4)),
        L(TInt(), Int(5), Int(3)).zip(L(TInt(), Int(2), Int(-1)), BinOp()));
  }

  @Test
  public void testSemanticsZip1() {
    // zip
    assertEquals(L(TInt(), Int(3), Int(1)), L(TInt(), Int(5), Int(3)).zip(S(Int(2)), BinOp()));
  }

  @Test
  public void testSemanticsZip2() {
    // zip
    assertEquals(L(TInt(), Int(-3), Int(-1)), S(Int(2)).zip(L(TInt(), Int(5), Int(3)), BinOp()));
  }

  @Test(expected = TypeCheckerException.class)
  public void testSemanticsZip3() {
    // zip null
    assertEquals(L(TInt(), Int(), Int()),
        L(TInt(), Int(), Int(1)).zip(L(TInt(), Int(), Int(1)), BinOp()));
  }

  @Test
  public void testSemanticsZip4() {
    // zip null
    assertEquals(L(TInt(), Int(), Int()), L(TInt(), Int(), Int(1)).zip(S(Int()), BinOp()));
  }

  @Test
  public void testSemanticsZip5() {
    // zip null
    assertEquals(L(TInt(), Int(), Int()), S(Int()).zip(L(TInt(), Int(), Int(3)), BinOp()));
  }

  @Test
  public void testAggregators() {
    assertEquals(C(TInt(), Int(2)).aggregate().getClass(),
        L(TInt(), Int(2)).aggregate().getClass());
    assertNotSame(AggregatingValueDefault.class, C(TInt(), Int(2)).aggregate().getClass());
  }
}
