package stupaq.cloudatlas.interpreter.types;

import com.google.common.collect.FluentIterable;

import org.junit.Test;

import stupaq.cloudatlas.attribute.AttributeValue;
import stupaq.cloudatlas.attribute.types.CAInteger;
import stupaq.cloudatlas.attribute.types.CAList;
import stupaq.cloudatlas.attribute.types.CASet;
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
import static stupaq.cloudatlas.interpreter.types.SemanticValueTestUtils.S;
import static stupaq.cloudatlas.interpreter.types.SemanticValueTestUtils.Set;
import static stupaq.cloudatlas.interpreter.types.SemanticValueTestUtils.UnOp;

public class RCollectionTest {
  @Test
  public void testSemanticsMap() {
    // map
    assertEquals(C(Int(-2), Int(-3)), C(Int(2), Int(3)).map(UnOp()));
  }

  @Test
  public void testSemanticsZip() {
    // zip
    assertEquals(C(Int(3), Int(4)), C(Int(5), Int(3)).zip(C(Int(2), Int(-1)), BinOp()));
    assertEquals(C(Int(3), Int(1)), C(Int(5), Int(3)).zip(S(Int(2)), BinOp()));
    assertEquals(C(Int(-3), Int(-1)), S(Int(2)).zip(C(Int(5), Int(3)), BinOp()));
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
    // count
    assertEquals(S(Int(2)), C(Int(2), Int(6)).aggregate().count());
    // first
    assertEquals(S(List(Int(1))), C(Int(1), Int(2)).aggregate().first(1));
    assertEquals(S(List(Int(1))), C(Int(1)).aggregate().first(2));
    // last
    assertEquals(S(List(Int(2))), C(Int(1), Int(2)).aggregate().last(1));
    assertEquals(S(List(Int(1))), C(Int(1)).aggregate().last(2));
    // random
    RCollection<CAInteger> collection = C(Int(1), Int(2), Int(3));
    RSingle<CAList<AttributeValue>> sample =
        (RSingle<CAList<AttributeValue>>) collection.aggregate().random(2);
    assertTrue(collection.containsAll(
        FluentIterable.from(sample.get()).transform(Optionals.<AttributeValue>optionalOf())
            .toList()));
    assertEquals(Int(2), sample.get().op().size());
    // min
    assertEquals(S(Int(2)), C(Int(4), Int(2), Int(4), Int(3)).aggregate().min());
    // max
    assertEquals(S(Int(4)), C(Int(4), Int(2), Int(4), Int(3)).aggregate().max());
    // land
    assertEquals(S(Bool(true)), C(Bool(true), Bool(true)).aggregate().land());
    assertEquals(S(Bool(false)), C(Bool(false), Bool(true)).aggregate().land());
    // lor
    assertEquals(S(Bool(true)), C(Bool(true), Bool(true)).aggregate().lor());
    assertEquals(S(Bool(true)), C(Bool(false), Bool(true)).aggregate().lor());
    // distinct
    assertEquals(L(Int(2), Int(3)), L(Int(2), Int(3), Int(3), Int(2)).aggregate().distinct());
    // unfold
    assertEquals(L(Int(2), Int(3), Int(3)),
        L(List(Int(2), Int(3)), new CAList<CAInteger>(), List(Int(3))).aggregate().unfold());
    assertEquals(L(Int(3), Int(3)),
        C(Set(Int(3), Int(3)), new CASet<CAInteger>(), Set(Int(3))).aggregate().unfold());
  }
}
