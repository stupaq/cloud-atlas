package stupaq.compact;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public enum TypeDescriptor {
  // These names match those known from CloudAtlas package 'by the accident',
  // we need a compile time association between type that can be automatically
  // serialized/deserialized and actual serializer class
  // ? extends AttributeValue
  CABoolean,
  CAContact,
  CADouble,
  CADuration,
  CAInteger,
  CAList,
  CAQuery,
  CASet,
  CAString,
  CATime,
  // AttributeName
  AttributeName,
  // Attribute
  Attribute,
  // TypeInfo
  TypeInfo,
  ComposedTypeInfo,
  // .naming package
  LocalName,
  GlobalName,
  // ZMI
  ZoneManagementInfo,
  // Gossips
  ZonesUpdateGossip,
  ZonesInterestGossip,
  ZonesInterestInitialGossip,
  SessionAcknowledgedGossip;

  static {
    assert TypeDescriptor.values().length < Byte.MAX_VALUE;
  }

  public static TypeDescriptor readInstance(DataInput in) throws IOException {
    int ordinal = in.readByte();
    return TypeDescriptor.values()[ordinal];
  }

  public static void writeInstance(DataOutput out, TypeDescriptor type) throws IOException {
    out.writeByte(type.ordinal());
  }
}
