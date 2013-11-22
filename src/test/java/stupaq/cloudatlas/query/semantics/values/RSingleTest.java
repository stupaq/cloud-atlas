package stupaq.cloudatlas.query.semantics.values;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static stupaq.cloudatlas.attribute.types.AttributeValueTestUtils.Bool;
import static stupaq.cloudatlas.attribute.types.AttributeValueTestUtils.Int;
import static stupaq.cloudatlas.query.typecheck.TypeInfoTestUtils.TInt;
import static stupaq.cloudatlas.query.semantics.values.SemanticValueTestUtils.BinOp;
import static stupaq.cloudatlas.query.semantics.values.SemanticValueTestUtils.C;
import static stupaq.cloudatlas.query.semantics.values.SemanticValueTestUtils.S;
import static stupaq.cloudatlas.query.semantics.values.SemanticValueTestUtils.UnOp;

public class RSingleTest {
  @Test
  public void testSemanticsNull() {
    assertEquals(S(Bool(true)), S(Int()).isNull());
    assertEquals(S(Bool(false)), S(Int(3)).isNull());
  }

  @Test
  public void testSemanticsMap() {
    // map
    assertEquals(S(Int(-2)), S(Int(2)).map(UnOp()));
    // map null
    assertEquals(S(Int()), S(Int()).map(UnOp()));
  }

  @Test
  public void testSemanticsZip() {
    // zip
    assertEquals(S(Int(4)), S(Int(5)).zip(S(Int(1)), BinOp()));
    assertEquals(C(TInt(), Int(3), Int(1)), C(TInt(), Int(5), Int(3)).zip(S(Int(2)), BinOp()));
    assertEquals(C(TInt(), Int(-3), Int(-1)), S(Int(2)).zip(C(TInt(), Int(5), Int(3)), BinOp()));
    // zip null
    assertEquals(S(Int()), S(Int(5)).zip(S(Int()), BinOp()));
    assertEquals(S(Int()), S(Int()).zip(S(Int(1)), BinOp()));
    assertEquals(S(Int()), S(Int()).zip(S(Int()), BinOp()));
    assertEquals(C(TInt(), Int(), Int()), C(TInt(), Int(5), Int(3)).zip(S(Int()), BinOp()));
    assertEquals(C(TInt(), Int(), Int()), S(Int()).zip(C(TInt(), Int(5), Int(3)), BinOp()));
    assertEquals(C(TInt(), Int(), Int(2)), C(TInt(), Int(), Int(3)).zip(S(Int(1)), BinOp()));
    assertEquals(C(TInt(), Int(), Int(-2)), S(Int(1)).zip(C(TInt(), Int(), Int(3)), BinOp()));
    assertEquals(C(TInt(), Int(), Int()), C(TInt(), Int(), Int(3)).zip(S(Int()), BinOp()));
    assertEquals(C(TInt(), Int(), Int()), S(Int()).zip(C(TInt(), Int(), Int(3)), BinOp()));
  }

  @Test(expected = Exception.class)
  public void testAggregators() {
    S(Int(0)).aggregate().avg();
    S(Int(1)).aggregate().avg();
  }
}
