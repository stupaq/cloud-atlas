package stupaq.cloudatlas.interpreter.values;

import org.junit.Test;

import stupaq.cloudatlas.interpreter.errors.EvaluationException;
import stupaq.cloudatlas.interpreter.semantics.AggregatingValue.AggregatingValueDefault;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static stupaq.cloudatlas.attribute.types.AttributeTypeTestUtils.Bool;
import static stupaq.cloudatlas.attribute.types.AttributeTypeTestUtils.Int;
import static stupaq.cloudatlas.interpreter.TypeInfoTestUtils.TBool;
import static stupaq.cloudatlas.interpreter.TypeInfoTestUtils.TInt;
import static stupaq.cloudatlas.interpreter.values.SemanticValueTestUtils.*;

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

  @Test(expected = EvaluationException.class)
  public void testSemanticsZip() {
    // zip
    assertEquals(L(TInt(), Int(3), Int(4)),
        L(TInt(), Int(5), Int(3)).zip(L(TInt(), Int(2), Int(-1)), BinOp()));
    assertEquals(L(TInt(), Int(3), Int(1)), L(TInt(), Int(5), Int(3)).zip(S(Int(2)), BinOp()));
    assertEquals(L(TInt(), Int(-3), Int(-1)), S(Int(2)).zip(L(TInt(), Int(5), Int(3)), BinOp()));
    // zip null
    assertEquals(L(TInt(), Int(), Int()),
        L(TInt(), Int(), Int(1)).zip(L(TInt(), Int(), Int(1)), BinOp()));
    assertEquals(L(TInt(), Int(), Int()), L(TInt(), Int(), Int(1)).zip(S(Int()), BinOp()));
    assertEquals(L(TInt(), Int(), Int()), S(Int()).zip(L(TInt(), Int(), Int(3)), BinOp()));
  }

  @Test
  public void testAggregators() {
    assertEquals(C(TInt(), Int(2)).aggregate().getClass(),
        L(TInt(), Int(2)).aggregate().getClass());
    assertNotSame(AggregatingValueDefault.class, C(TInt(), Int(2)).aggregate().getClass());
  }
}
