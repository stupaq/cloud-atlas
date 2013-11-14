package stupaq.cloudatlas.interpreter.types;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static stupaq.cloudatlas.interpreter.types.SemanticValueTestUtils.BinOp;
import static stupaq.cloudatlas.interpreter.types.SemanticValueTestUtils.C;
import static stupaq.cloudatlas.interpreter.types.SemanticValueTestUtils.Int;
import static stupaq.cloudatlas.interpreter.types.SemanticValueTestUtils.S;
import static stupaq.cloudatlas.interpreter.types.SemanticValueTestUtils.UnOp;

public class RSingleTest {
  @Test
  public void testSemanticsMap() {
    // map
    assertEquals(S(Int(-2)), S(Int(2)).map(UnOp()));
  }

  @Test
  public void testSemanticsZip() {
    // zip
    assertEquals(S(Int(4)), S(Int(5)).zip(S(Int(1)), BinOp()));
    assertEquals(C(Int(3), Int(1)), C(Int(5), Int(3)).zip(S(Int(2)), BinOp()));
    assertEquals(C(Int(-3), Int(-1)), S(Int(2)).zip(C(Int(5), Int(3)), BinOp()));
  }
}
