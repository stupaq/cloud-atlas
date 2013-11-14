package stupaq.cloudatlas.interpreter.types;

import org.junit.Test;

import stupaq.cloudatlas.interpreter.errors.EvaluationException;
import stupaq.cloudatlas.interpreter.semantics.AggregatingValue.AggregatingValueDefault;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static stupaq.cloudatlas.interpreter.types.SemanticValueTestUtils.BinOp;
import static stupaq.cloudatlas.interpreter.types.SemanticValueTestUtils.C;
import static stupaq.cloudatlas.interpreter.types.SemanticValueTestUtils.Int;
import static stupaq.cloudatlas.interpreter.types.SemanticValueTestUtils.L;
import static stupaq.cloudatlas.interpreter.types.SemanticValueTestUtils.S;
import static stupaq.cloudatlas.interpreter.types.SemanticValueTestUtils.UnOp;

public class RListTest {
  @Test
  public void testSemanticsMap() {
    // map
    assertEquals(L(Int(-2), Int(-3)), L(Int(2), Int(3)).map(UnOp()));
    // map null
    assertEquals(L(null, Int(-3)), L(null, Int(3)).map(UnOp()));
  }

  @Test(expected = EvaluationException.class)
  public void testSemanticsZip() {
    // zip
    assertEquals(L(Int(3), Int(4)), L(Int(5), Int(3)).zip(L(Int(2), Int(-1)), BinOp()));
    assertEquals(L(Int(3), Int(1)), L(Int(5), Int(3)).zip(S(Int(2)), BinOp()));
    assertEquals(L(Int(-3), Int(-1)), S(Int(2)).zip(L(Int(5), Int(3)), BinOp()));
    // zip null
    assertEquals(L(null, null), L(null, Int(1)).zip(L(null, Int(1)), BinOp()));
    assertEquals(L(null, null), L(null, Int(1)).zip(S(Int()), BinOp()));
    assertEquals(L(null, null), S(Int()).zip(L(null, Int(3)), BinOp()));
  }

  @Test
  public void testAggregators() {
    assertEquals(C(Int(2)).aggregate().getClass(), L(Int(2)).aggregate().getClass());
    assertNotSame(AggregatingValueDefault.class, C(Int(2)).aggregate().getClass());
  }
}
