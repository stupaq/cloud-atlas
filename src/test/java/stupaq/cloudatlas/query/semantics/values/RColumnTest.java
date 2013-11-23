package stupaq.cloudatlas.query.semantics.values;

import org.junit.Test;

import stupaq.cloudatlas.query.semantics.AggregatingValue.AggregatingValueDefault;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static stupaq.cloudatlas.attribute.values.AttributeValueTestUtils.Bool;
import static stupaq.cloudatlas.attribute.values.AttributeValueTestUtils.Int;
import static stupaq.cloudatlas.query.semantics.values.SemanticValueTestUtils.*;
import static stupaq.cloudatlas.query.typecheck.TypeInfoTestUtils.TBool;
import static stupaq.cloudatlas.query.typecheck.TypeInfoTestUtils.TInt;

public class RColumnTest {
  @Test
  public void testSemanticsNull() {
    assertEquals(C(TBool(), Bool(true), Bool(false), Bool(false)),
        C(TInt(), Int(), Int(2), Int(3)).isNull());
    assertEquals(C(TBool()), C(TBool()).isNull());
  }

  @Test
  public void testSemanticsMap() {
    // map
    assertEquals(C(TInt(), Int(-2), Int(-3)), C(TInt(), Int(2), Int(3)).map(UnOp()));
    // map null
    assertEquals(C(TInt(), Int(-2), Int(), Int(-3)), C(TInt(), Int(2), Int(), Int(3)).map(UnOp()));
    assertEquals(C(TInt(), Int(), Int(), Int()), C(TInt(), Int(), Int(), Int()).map(UnOp()));
  }

  @Test
  public void testSemanticsZip() {
    // zip
    assertEquals(C(TInt(), Int(3), Int(4)),
        C(TInt(), Int(5), Int(3)).zip(C(TInt(), Int(2), Int(-1)), BinOp()));
    assertEquals(C(TInt(), Int(3), Int(1)), C(TInt(), Int(5), Int(3)).zip(S(Int(2)), BinOp()));
    assertEquals(C(TInt(), Int(-3), Int(-1)), S(Int(2)).zip(C(TInt(), Int(5), Int(3)), BinOp()));
    // zip null
    assertEquals(C(TInt(), Int(), Int()), C(TInt(), Int(5), Int(3)).zip(S(Int()), BinOp()));
    assertEquals(C(TInt(), Int(), Int()), S(Int()).zip(C(TInt(), Int(5), Int(3)), BinOp()));
    assertEquals(C(TInt(), Int(-3), Int()), S(Int(2)).zip(C(TInt(), Int(5), Int()), BinOp()));
    assertEquals(C(TInt(), Int(), Int()),
        C(TInt(), Int(), Int(3)).zip(C(TInt(), Int(2), Int()), BinOp()));
    assertEquals(C(TInt(), Int(), Int()),
        C(TInt(), Int(5), Int()).zip(C(TInt(), Int(), Int(-1)), BinOp()));
    assertEquals(C(TInt(), Int(), Int()),
        C(TInt(), Int(), Int()).zip(C(TInt(), Int(2), Int(2)), BinOp()));
    assertEquals(C(TInt(), Int(), Int()),
        C(TInt(), Int(), Int()).zip(C(TInt(), Int(), Int()), BinOp()));
  }

  @Test
  public void testAggregators() {
    assertEquals(C(TInt(), Int(2)).aggregate().getClass(),
        L(TInt(), Int(2)).aggregate().getClass());
    assertNotSame(AggregatingValueDefault.class, C(TInt(), Int(2)).aggregate().getClass());
  }
}
