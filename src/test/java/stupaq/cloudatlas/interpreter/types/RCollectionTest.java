package stupaq.cloudatlas.interpreter.types;

import com.google.common.collect.FluentIterable;

import org.junit.Test;

import stupaq.cloudatlas.attribute.AttributeValue;
import stupaq.cloudatlas.attribute.types.CAList;
import stupaq.guava.base.Optionals;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static stupaq.cloudatlas.interpreter.types.SemanticValueTestUtils.BinOp;
import static stupaq.cloudatlas.interpreter.types.SemanticValueTestUtils.Bool;
import static stupaq.cloudatlas.interpreter.types.SemanticValueTestUtils.C;
import static stupaq.cloudatlas.interpreter.types.SemanticValueTestUtils.Doub;
import static stupaq.cloudatlas.interpreter.types.SemanticValueTestUtils.Int;
import static stupaq.cloudatlas.interpreter.types.SemanticValueTestUtils.L;
import static stupaq.cloudatlas.interpreter.types.SemanticValueTestUtils.List;
import static stupaq.cloudatlas.interpreter.types.SemanticValueTestUtils.ListEmpty;
import static stupaq.cloudatlas.interpreter.types.SemanticValueTestUtils.ListNull;
import static stupaq.cloudatlas.interpreter.types.SemanticValueTestUtils.S;
import static stupaq.cloudatlas.interpreter.types.SemanticValueTestUtils.Set;
import static stupaq.cloudatlas.interpreter.types.SemanticValueTestUtils.SetEmpty;
import static stupaq.cloudatlas.interpreter.types.SemanticValueTestUtils.UnOp;

public class RCollectionTest {
  private void testRandom(RCollection<? extends AttributeValue> collection, int sampleSize,
      int resultSize) {
    RSingle<CAList<AttributeValue>> sample =
        (RSingle<CAList<AttributeValue>>) collection.aggregate().random(sampleSize);
    if (resultSize < 0) {
      assertEquals(S(ListNull()), sample);
    } else {
      assertTrue(collection.containsAll(
          FluentIterable.from(sample.get()).transform(Optionals.<AttributeValue>optionalOf())
              .toList()));
      assertEquals(Int(resultSize), sample.get().op().size());
    }
  }

  @Test
  public void testSemanticsNull() {
    assertEquals(C(Bool(true), Bool(false), Bool(false)), C(Int(), Int(2), Int(3)).isNull());
    assertEquals(C(), C().isNull());
  }

  @Test
  public void testSemanticsMap() {
    // map
    assertEquals(C(Int(-2), Int(-3)), C(Int(2), Int(3)).map(UnOp()));
    // map null
    assertEquals(C(Int(-2), Int(), Int(-3)), C(Int(2), Int(), Int(3)).map(UnOp()));
    assertEquals(C(Int(), Int(), Int()), C(Int(), Int(), Int()).map(UnOp()));
  }

  @Test
  public void testSemanticsZip() {
    // zip
    assertEquals(C(Int(3), Int(4)), C(Int(5), Int(3)).zip(C(Int(2), Int(-1)), BinOp()));
    assertEquals(C(Int(3), Int(1)), C(Int(5), Int(3)).zip(S(Int(2)), BinOp()));
    assertEquals(C(Int(-3), Int(-1)), S(Int(2)).zip(C(Int(5), Int(3)), BinOp()));
    // zip null
    assertEquals(C(Int(), Int()), C(Int(5), Int(3)).zip(S(Int()), BinOp()));
    assertEquals(C(Int(), Int()), S(Int()).zip(C(Int(5), Int(3)), BinOp()));
    assertEquals(C(Int(-3), Int()), S(Int(2)).zip(C(Int(5), Int()), BinOp()));
    assertEquals(C(Int(), Int()), C(Int(), Int(3)).zip(C(Int(2), Int()), BinOp()));
    assertEquals(C(Int(), Int()), C(Int(5), Int()).zip(C(Int(), Int(-1)), BinOp()));
    assertEquals(C(Int(), Int()), C(Int(), Int()).zip(C(Int(2), Int(2)), BinOp()));
    assertEquals(C(Int(), Int()), C(Int(), Int()).zip(C(Int(), Int()), BinOp()));
  }

  @Test
  public void testAggregators() {
    // avg
    assertEquals(S(Doub(4)), C(Int(2), Int(6)).aggregate().avg());
    // avg null
    assertEquals(S(Doub(2)), C(Doub(), Doub(2)).aggregate().avg());
    assertEquals(S(Doub()), C(Doub(), Doub()).aggregate().avg());
    assertEquals(S(Doub()), C().aggregate().avg());
    // sum
    assertEquals(S(Int(8)), C(Int(2), Int(6)).aggregate().sum());
    assertEquals(S(Doub(8)), C(Doub(2), Doub(6)).aggregate().sum());
    assertEquals(S(Int(0)), C().aggregate().sum());
    // sum null
    assertEquals(S(Int(2)), C(Int(2), Int()).aggregate().sum());
    assertEquals(S(Doub()), C(Doub(), Doub()).aggregate().sum());
    // count
    assertEquals(S(Int(2)), C(Int(2), Int(6)).aggregate().count());
    assertEquals(S(Int(0)), C().aggregate().count());
    // count null
    assertEquals(S(Int(1)), C(Int(), Int(6)).aggregate().count());
    assertEquals(S(Int()), C(Int(), Int()).aggregate().count());
    // first
    assertEquals(S(List(Int(1))), C(Int(1), Int(2)).aggregate().first(1));
    assertEquals(S(List(Int(1), Int(1))), C(Int(1), Int(1), Int(2)).aggregate().first(2));
    assertEquals(S(List(Int(1))), C(Int(1)).aggregate().first(2));
    // first null
    assertEquals(S(List(Int(2))), C(Int(), Int(2)).aggregate().first(1));
    assertEquals(S(List(Int(1), Int(2))), C(Int(1), Int(), Int(2)).aggregate().first(2));
    assertEquals(S(ListNull()), C(Int(), Int()).aggregate().first(1));
    assertEquals(S(List(Int(1))), C(Int(), Int(1)).aggregate().first(2));
    // last
    assertEquals(S(List(Int(2))), C(Int(1), Int(2)).aggregate().last(1));
    assertEquals(S(List(Int(1))), C(Int(1)).aggregate().last(2));
    // last null
    assertEquals(S(List(Int(1))), C(Int(1), Int()).aggregate().last(1));
    assertEquals(S(List(Int(1), Int(2))), C(Int(1), Int(), Int(2)).aggregate().last(2));
    assertEquals(S(List(Int(1))), C(Int(1), Int(), Int()).aggregate().last(2));
    assertEquals(S(ListNull()), C(Int(), Int()).aggregate().last(1));
    // random
    testRandom(C(Int(1), Int(2), Int(3)), 2, 2);
    // random null
    testRandom(C(Int(1), Int(), Int(3)), 2, 2);
    testRandom(C(Int(), Int(), Int(3)), 2, 1);
    testRandom(C(Int(), Int(), Int()), 2, -1);
    // min
    assertEquals(S(Int(2)), C(Int(4), Int(2), Int(4), Int(3)).aggregate().min());
    // min null
    assertEquals(S(Int(3)), C(Int(), Int(), Int(4), Int(3)).aggregate().min());
    assertEquals(S(Int()), C(Int(), Int(), Int(), Int()).aggregate().min());
    assertEquals(S(Int()), C().aggregate().min());
    // max
    assertEquals(S(Int(4)), C(Int(4), Int(2), Int(4), Int(3)).aggregate().max());
    // max null
    assertEquals(S(Int(4)), C(Int(), Int(), Int(4), Int(3)).aggregate().max());
    assertEquals(S(Int()), C(Int(), Int(), Int(), Int()).aggregate().max());
    assertEquals(S(Int()), C().aggregate().max());
    // land
    assertEquals(S(Bool(true)), C(Bool(true), Bool(true)).aggregate().land());
    assertEquals(S(Bool(false)), C(Bool(false), Bool(true)).aggregate().land());
    assertEquals(S(Bool(true)), C().aggregate().land());
    // land null
    assertEquals(S(Bool(true)), C(Bool(true), Bool(true)).aggregate().land());
    assertEquals(S(Bool(true)), C(Bool(), Bool(true)).aggregate().land());
    assertEquals(S(Bool(false)), C(Bool(), Bool(false)).aggregate().land());
    assertEquals(S(Bool(false)), C(Bool(), Bool(true), Bool(false)).aggregate().land());
    assertEquals(S(Bool(false)), C(Bool(false), Bool()).aggregate().land());
    assertEquals(S(Bool()), C(Bool(), Bool()).aggregate().land());
    // lor
    assertEquals(S(Bool(true)), C(Bool(true), Bool(true)).aggregate().lor());
    assertEquals(S(Bool(true)), C(Bool(false), Bool(true)).aggregate().lor());
    assertEquals(S(Bool(false)), C().aggregate().lor());
    // lor null
    assertEquals(S(Bool(true)), C(Bool(true), Bool(true)).aggregate().lor());
    assertEquals(S(Bool(true)), C(Bool(), Bool(true)).aggregate().lor());
    assertEquals(S(Bool(false)), C(Bool(), Bool(false)).aggregate().lor());
    assertEquals(S(Bool(true)), C(Bool(), Bool(false), Bool(true)).aggregate().lor());
    assertEquals(S(Bool(false)), C(Bool(false), Bool()).aggregate().lor());
    assertEquals(S(Bool()), C(Bool(), Bool()).aggregate().lor());
    // distinct
    assertEquals(L(Int(2), Int(3)), L(Int(2), Int(3), Int(3), Int(2)).aggregate().distinct());
    // distinct null
    assertEquals(L(Int(3), Int(2)), L(Int(), Int(3), Int(3), Int(2)).aggregate().distinct());
    assertEquals(L(Int(3)), L(Int(), Int(3), Int(3), Int()).aggregate().distinct());
    assertEquals(L(), L(Int(), Int(), Int(), Int()).aggregate().distinct());
    // unfold
    assertEquals(L(Int(2), Int(3), Int(3)),
        L(List(Int(2), Int(3)), ListEmpty(), List(Int(3))).aggregate().unfold());
    assertEquals(L(Int(2), Int(3), Int(3)),
        L(Set(Int(2), Int(3)), SetEmpty(), Set(Int(3))).aggregate().unfold());
    assertEquals(L(Int(3), Int(3), Int(3)),
        C(List(Int(3), Int(3)), ListEmpty(), List(Int(3))).aggregate().unfold());
    assertEquals(L(Int(3), Int(3)),
        C(Set(Int(3), Int(3)), SetEmpty(), Set(Int(3))).aggregate().unfold());
    assertEquals(L(), C().aggregate().unfold());
    // unfold null
    assertEquals(L(Int(3), Int(3), Int(3)),
        C(List(Int(3), Int(3)), ListNull(), List(Int(3))).aggregate().unfold());
    assertEquals(L(Int(3)), C(ListNull(), ListNull(), List(Int(3))).aggregate().unfold());
    assertEquals(S(ListNull()), C(ListNull(), ListNull()).aggregate().unfold());
  }
}
