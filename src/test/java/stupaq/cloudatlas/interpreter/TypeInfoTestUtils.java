package stupaq.cloudatlas.interpreter;

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
import stupaq.cloudatlas.interpreter.typecheck.ComposedTypeInfo;
import stupaq.cloudatlas.interpreter.typecheck.TypeInfo;

public class TypeInfoTestUtils {
  private TypeInfoTestUtils() {
  }

  public static TypeInfo<CAInteger> TInt() {
    return new TypeInfo<>(CAInteger.class);
  }

  public static TypeInfo<CADuration> TDur() {
    return new TypeInfo<>(CADuration.class);
  }

  public static TypeInfo<CATime> TTime() {
    return new TypeInfo<>(CATime.class);
  }

  public static TypeInfo<CADouble> TDoub() {
    return new TypeInfo<>(CADouble.class);
  }

  public static TypeInfo<CABoolean> TBool() {
    return new TypeInfo<>(CABoolean.class);
  }

  public static TypeInfo<CAString> TStr() {
    return new TypeInfo<>(CAString.class);
  }

  public static TypeInfo<CAContact> TCont() {
    return new TypeInfo<>(CAContact.class);
  }

  public static <Enclosing extends AttributeValue> ComposedTypeInfo<CAList> TList(
      TypeInfo<Enclosing> enclosing) {
    return new ComposedTypeInfo<>(CAList.class, enclosing);
  }

  public static <Enclosing extends AttributeValue> ComposedTypeInfo<CASet> TSet(
      TypeInfo<Enclosing> enclosing) {
    return new ComposedTypeInfo<>(CASet.class, enclosing);
  }
}
