package stupaq.cloudatlas.query.typecheck;

import stupaq.cloudatlas.attribute.AttributeValue;
import stupaq.cloudatlas.attribute.types.CABoolean;
import stupaq.cloudatlas.attribute.types.CAContact;
import stupaq.cloudatlas.attribute.types.CADouble;
import stupaq.cloudatlas.attribute.types.CADuration;
import stupaq.cloudatlas.attribute.types.CAInteger;
import stupaq.cloudatlas.attribute.types.CAList;
import stupaq.cloudatlas.attribute.types.CASet;
import stupaq.cloudatlas.attribute.types.CAString;
import stupaq.cloudatlas.attribute.types.CATime;

public class TypeInfoTestUtils {
  private TypeInfoTestUtils() {
  }

  public static TypeInfo<CAInteger> TInt() {
    return TypeInfo.of(CAInteger.class);
  }

  public static TypeInfo<CADuration> TDur() {
    return TypeInfo.of(CADuration.class);
  }

  public static TypeInfo<CATime> TTime() {
    return TypeInfo.of(CATime.class);
  }

  public static TypeInfo<CADouble> TDoub() {
    return TypeInfo.of(CADouble.class);
  }

  public static TypeInfo<CABoolean> TBool() {
    return TypeInfo.of(CABoolean.class);
  }

  public static TypeInfo<CAString> TStr() {
    return TypeInfo.of(CAString.class);
  }

  public static TypeInfo<CAContact> TCont() {
    return TypeInfo.of(CAContact.class);
  }

  public static <Enclosing extends AttributeValue> ComposedTypeInfo<CAList> TList(
      TypeInfo<Enclosing> enclosing) {
    return ComposedTypeInfo.of(CAList.class, enclosing);
  }

  public static <Enclosing extends AttributeValue> ComposedTypeInfo<CASet> TSet(
      TypeInfo<Enclosing> enclosing) {
    return ComposedTypeInfo.of(CASet.class, enclosing);
  }
}
