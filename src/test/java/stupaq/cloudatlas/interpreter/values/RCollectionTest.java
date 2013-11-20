package stupaq.cloudatlas.interpreter.values;

import org.junit.Test;

import stupaq.cloudatlas.attribute.AttributeValue;
import stupaq.cloudatlas.attribute.types.CAList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static stupaq.cloudatlas.attribute.types.AttributeTypeTestUtils.*;
import static stupaq.cloudatlas.interpreter.typecheck.TypeInfoTestUtils.*;
import static stupaq.cloudatlas.interpreter.values.SemanticValueTestUtils.*;

public class RCollectionTest {
  private void testRandom(RCollection<? extends AttributeValue> collection, int sampleSize,
      int resultSize) {
    RSingle<CAList<AttributeValue>> sample =
        (RSingle<CAList<AttributeValue>>) collection.aggregate().random(Int(sampleSize));
    if (resultSize < 0) {
      assertEquals(S(ListNull(collection.getType())), sample);
    } else {
      assertTrue(collection.containsAll(sample.get().asImmutableList()));
      assertEquals(Int(resultSize), sample.get().op().size());
    }
  }

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
    // avg
    assertEquals(S(Doub(4)), C(TInt(), Int(2), Int(6)).aggregate().avg());
    // avg null
    assertEquals(S(Doub(2)), C(TDoub(), Doub(), Doub(2)).aggregate().avg());
    assertEquals(S(Doub()), C(TDoub(), Doub(), Doub()).aggregate().avg());
    assertEquals(S(Doub()), C(TStr()).aggregate().avg());
    // sum
    assertEquals(S(Int(8)), C(TInt(), Int(2), Int(6)).aggregate().sum());
    assertEquals(S(Doub(8)), C(TDoub(), Doub(2), Doub(6)).aggregate().sum());
    assertEquals(S(Int(0)), C(TInt()).aggregate().sum());
    // sum null
    assertEquals(S(Int(2)), C(TInt(), Int(2), Int()).aggregate().sum());
    assertEquals(S(Doub()), C(TDoub(), Doub(), Doub()).aggregate().sum());
    // count
    assertEquals(S(Int(2)), C(TInt(), Int(2), Int(6)).aggregate().count());
    assertEquals(S(Int(0)), C(TInt()).aggregate().count());
    // count null
    assertEquals(S(Int(1)), C(TInt(), Int(), Int(6)).aggregate().count());
    assertEquals(S(Int()), C(TInt(), Int(), Int()).aggregate().count());
    // first
    assertEquals(S(List(TInt(), Int(1))), C(TInt(), Int(1), Int(2)).aggregate().first(Int(1)));
    assertEquals(S(List(TInt(), Int(1), Int(1))),
        C(TInt(), Int(1), Int(1), Int(2)).aggregate().first(Int(2)));
    assertEquals(S(List(TInt(), Int(1))), C(TInt(), Int(1)).aggregate().first(Int(2)));
    // first null
    assertEquals(S(List(TInt(), Int(2))), C(TInt(), Int(), Int(2)).aggregate().first(Int(1)));
    assertEquals(S(List(TInt(), Int(1), Int(2))),
        C(TInt(), Int(1), Int(), Int(2)).aggregate().first(Int(2)));
    assertEquals(S(ListNull(TInt())), C(TInt(), Int(), Int()).aggregate().first(Int(1)));
    assertEquals(S(List(TInt(), Int(1))), C(TInt(), Int(), Int(1)).aggregate().first(Int(2)));
    // last
    assertEquals(S(List(TInt(), Int(2))), C(TInt(), Int(1), Int(2)).aggregate().last(Int(1)));
    assertEquals(S(List(TInt(), Int(1))), C(TInt(), Int(1)).aggregate().last(Int(2)));
    // last null
    assertEquals(S(List(TInt(), Int(1))), C(TInt(), Int(1), Int()).aggregate().last(Int(1)));
    assertEquals(S(List(TInt(), Int(1), Int(2))),
        C(TInt(), Int(1), Int(), Int(2)).aggregate().last(Int(2)));
    assertEquals(S(List(TInt(), Int(1))), C(TInt(), Int(1), Int(), Int()).aggregate().last(Int(2)));
    assertEquals(S(ListNull(TInt())), C(TInt(), Int(), Int()).aggregate().last(Int(1)));
    // random
    testRandom(C(TInt(), Int(1), Int(2), Int(3)), 2, 2);
    // random null
    testRandom(C(TInt(), Int(1), Int(), Int(3)), 2, 2);
    testRandom(C(TInt(), Int(), Int(), Int(3)), 2, 1);
    testRandom(C(TInt(), Int(), Int(), Int()), 2, -1);
    // min
    assertEquals(S(Int(2)), C(TInt(), Int(4), Int(2), Int(4), Int(3)).aggregate().min());
    // min null
    assertEquals(S(Int(3)), C(TInt(), Int(), Int(), Int(4), Int(3)).aggregate().min());
    assertEquals(S(Int()), C(TInt(), Int(), Int(), Int(), Int()).aggregate().min());
    assertEquals(S(Int()), C(TInt()).aggregate().min());
    // max
    assertEquals(S(Int(4)), C(TInt(), Int(4), Int(2), Int(4), Int(3)).aggregate().max());
    // max null
    assertEquals(S(Int(4)), C(TInt(), Int(), Int(), Int(4), Int(3)).aggregate().max());
    assertEquals(S(Int()), C(TInt(), Int(), Int(), Int(), Int()).aggregate().max());
    assertEquals(S(Int()), C(TInt()).aggregate().max());
    // land
    assertEquals(S(Bool(true)), C(TBool(), Bool(true), Bool(true)).aggregate().land());
    assertEquals(S(Bool(false)), C(TBool(), Bool(false), Bool(true)).aggregate().land());
    assertEquals(S(Bool(true)), C(TBool()).aggregate().land());
    // land null
    assertEquals(S(Bool(true)), C(TBool(), Bool(true), Bool(true)).aggregate().land());
    assertEquals(S(Bool(true)), C(TBool(), Bool(), Bool(true)).aggregate().land());
    assertEquals(S(Bool(false)), C(TBool(), Bool(), Bool(false)).aggregate().land());
    assertEquals(S(Bool(false)), C(TBool(), Bool(), Bool(true), Bool(false)).aggregate().land());
    assertEquals(S(Bool(false)), C(TBool(), Bool(false), Bool()).aggregate().land());
    assertEquals(S(Bool()), C(TBool(), Bool(), Bool()).aggregate().land());
    // lor
    assertEquals(S(Bool(true)), C(TBool(), Bool(true), Bool(true)).aggregate().lor());
    assertEquals(S(Bool(true)), C(TBool(), Bool(false), Bool(true)).aggregate().lor());
    assertEquals(S(Bool(false)), C(TBool()).aggregate().lor());
    // lor null
    assertEquals(S(Bool(true)), C(TBool(), Bool(true), Bool(true)).aggregate().lor());
    assertEquals(S(Bool(true)), C(TBool(), Bool(), Bool(true)).aggregate().lor());
    assertEquals(S(Bool(false)), C(TBool(), Bool(), Bool(false)).aggregate().lor());
    assertEquals(S(Bool(true)), C(TBool(), Bool(), Bool(false), Bool(true)).aggregate().lor());
    assertEquals(S(Bool(false)), C(TBool(), Bool(false), Bool()).aggregate().lor());
    assertEquals(S(Bool()), C(TBool(), Bool(), Bool()).aggregate().lor());
    // distinct
    assertEquals(L(TInt(), Int(2), Int(3)),
        L(TInt(), Int(2), Int(3), Int(3), Int(2)).aggregate().distinct());
    // distinct null
    assertEquals(L(TInt(), Int(3), Int(2)),
        L(TInt(), Int(), Int(3), Int(3), Int(2)).aggregate().distinct());
    assertEquals(L(TInt(), Int(3)), L(TInt(), Int(), Int(3), Int(3), Int()).aggregate().distinct());
    assertEquals(L(TInt()), L(TInt(), Int(), Int(), Int(), Int()).aggregate().distinct());
    // unfold
    assertEquals(L(TInt(), Int(2), Int(3), Int(3)),
        L(TList(TInt()), List(TInt(), Int(2), Int(3)), ListEmpty(TInt()), List(TInt(), Int(3)))
            .aggregate().unfold());
    assertEquals(L(TInt(), Int(2), Int(3), Int(3)),
        L(TSet(TInt()), Set(TInt(), Int(2), Int(3)), SetEmpty(TInt()), Set(TInt(), Int(3)))
            .aggregate().unfold());
    assertEquals(L(TInt(), Int(3), Int(3), Int(3)),
        C(TList(TInt()), List(TInt(), Int(3), Int(3)), ListEmpty(TInt()), List(TInt(), Int(3)))
            .aggregate().unfold());
    assertEquals(L(TInt(), Int(3), Int(3)),
        C(TSet(TInt()), Set(TInt(), Int(3), Int(3)), SetEmpty(TInt()), Set(TInt(), Int(3)))
            .aggregate().unfold());
    assertEquals(L(TInt()), C(TList(TInt())).aggregate().unfold());
    // unfold null
    assertEquals(L(TInt(), Int(3), Int(3), Int(3)),
        C(TList(TInt()), List(TInt(), Int(3), Int(3)), ListNull(TInt()), List(TInt(), Int(3)))
            .aggregate().unfold());
    assertEquals(L(TInt(), Int(3)),
        C(TList(TInt()), ListNull(TInt()), ListNull(TInt()), List(TInt(), Int(3))).aggregate()
            .unfold());
    assertEquals(S(Int()),
        C(TList(TInt()), ListNull(TInt()), ListNull(TInt())).aggregate().unfold());
  }
}
