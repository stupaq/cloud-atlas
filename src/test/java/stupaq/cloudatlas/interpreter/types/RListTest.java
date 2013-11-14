package stupaq.cloudatlas.interpreter.types;

import org.junit.Test;

import stupaq.cloudatlas.interpreter.errors.EvaluationException;

import static org.junit.Assert.assertEquals;
import static stupaq.cloudatlas.interpreter.types.SemanticValueTestUtils.BinOp;
import static stupaq.cloudatlas.interpreter.types.SemanticValueTestUtils.Int;
import static stupaq.cloudatlas.interpreter.types.SemanticValueTestUtils.L;
import static stupaq.cloudatlas.interpreter.types.SemanticValueTestUtils.S;
import static stupaq.cloudatlas.interpreter.types.SemanticValueTestUtils.UnOp;

public class RListTest {
  @Test
  public void testSemanticsMap() {
    // map
    assertEquals(L(Int(-2), Int(-3)), L(Int(2), Int(3)).map(UnOp()));
  }

  @Test(expected = EvaluationException.class)
  public void testSemanticsZip() {
    // zip
    assertEquals(L(Int(3), Int(4)), L(Int(5), Int(3)).zip(L(Int(2), Int(-1)), BinOp()));
    assertEquals(L(Int(3), Int(1)), L(Int(5), Int(3)).zip(S(Int(2)), BinOp()));
    assertEquals(L(Int(-3), Int(-1)), S(Int(2)).zip(L(Int(5), Int(3)), BinOp()));
  }
}
