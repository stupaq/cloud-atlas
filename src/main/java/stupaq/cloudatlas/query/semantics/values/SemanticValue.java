package stupaq.cloudatlas.query.semantics.values;

import stupaq.cloudatlas.attribute.AttributeValue;
import stupaq.cloudatlas.attribute.values.CABoolean;
import stupaq.cloudatlas.query.semantics.AggregatingValue;
import stupaq.cloudatlas.query.typecheck.TypeInfo;
import stupaq.commons.base.Function1;
import stupaq.commons.base.Function2;

public interface SemanticValue<Type extends AttributeValue> {

  public SemanticValue<CABoolean> isNull();

  public <Result extends AttributeValue> SemanticValue<Result> map(
      Function1<Type, Result> function);

  public <Other extends AttributeValue, Result extends AttributeValue> SemanticValue<Result> zip(
      SemanticValue<Other> second, Function2<Type, Other, Result> operation);

  public <Other extends AttributeValue, Result extends AttributeValue> SemanticValue<Result> zipWith(
      RColumn<Other> first, Function2<Other, Type, Result> operation);

  public <Other extends AttributeValue, Result extends AttributeValue> SemanticValue<Result> zipWith(
      RList<Other> first, Function2<Other, Type, Result> operation);

  public <Other extends AttributeValue, Result extends AttributeValue> SemanticValue<Result> zipWith(
      RSingle<Other> first, Function2<Other, Type, Result> operation);

  public RSingle<Type> getSingle() throws SemanticValueCastException;

  public AggregatingValue aggregate();

  public TypeInfo<Type> getType();

  public static class SemanticValueCastException extends Exception {
  }
}
