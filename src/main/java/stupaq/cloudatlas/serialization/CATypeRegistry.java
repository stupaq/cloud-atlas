package stupaq.cloudatlas.serialization;

import java.util.concurrent.atomic.AtomicBoolean;

import stupaq.cloudatlas.attribute.Attribute;
import stupaq.cloudatlas.attribute.values.CABoolean;
import stupaq.cloudatlas.attribute.values.CAContact;
import stupaq.cloudatlas.attribute.values.CADouble;
import stupaq.cloudatlas.attribute.values.CADuration;
import stupaq.cloudatlas.attribute.values.CAInteger;
import stupaq.cloudatlas.attribute.values.CAList;
import stupaq.cloudatlas.attribute.values.CAQuery;
import stupaq.cloudatlas.attribute.values.CASet;
import stupaq.cloudatlas.attribute.values.CAString;
import stupaq.cloudatlas.attribute.values.CATime;
import stupaq.cloudatlas.messaging.messages.gossips.SessionAcknowledgedGossip;
import stupaq.cloudatlas.messaging.messages.gossips.ZonesInterestInitialGossip;
import stupaq.cloudatlas.messaging.messages.gossips.ZonesInterestResponseGossip;
import stupaq.cloudatlas.messaging.messages.gossips.ZonesUpdateGossip;
import stupaq.cloudatlas.naming.AttributeName;
import stupaq.cloudatlas.naming.GlobalName;
import stupaq.cloudatlas.naming.LocalName;
import stupaq.cloudatlas.query.typecheck.ComposedTypeInfo;
import stupaq.cloudatlas.query.typecheck.TypeInfo;
import stupaq.cloudatlas.services.zonemanager.ZoneManagementInfo;
import stupaq.compact.TypeDescriptor;

import static stupaq.compact.TypeRegistry.register;

public final class CATypeRegistry {
  private CATypeRegistry() {
  }

  private static final AtomicBoolean singleCallGuard = new AtomicBoolean(false);

  public static void registerCATypes() {
    if (singleCallGuard.getAndSet(true)) {
      return;
    }
    // Register all serializable types that we use in CloudAtlas
    // ? extends AttributeValue
    register(TypeDescriptor.CABoolean, CABoolean.SERIALIZER);
    register(TypeDescriptor.CAContact, CAContact.SERIALIZER);
    register(TypeDescriptor.CADouble, CADouble.SERIALIZER);
    register(TypeDescriptor.CADuration, CADuration.SERIALIZER);
    register(TypeDescriptor.CAInteger, CAInteger.SERIALIZER);
    register(TypeDescriptor.CAList, CAList.SERIALIZER);
    register(TypeDescriptor.CAQuery, CAQuery.SERIALIZER);
    register(TypeDescriptor.CASet, CASet.SERIALIZER);
    register(TypeDescriptor.CAString, CAString.SERIALIZER);
    register(TypeDescriptor.CATime, CATime.SERIALIZER);
    // AttributeName
    register(TypeDescriptor.AttributeName, AttributeName.SERIALIZER);
    // Attribute
    register(TypeDescriptor.Attribute, Attribute.SERIALIZER);
    // TypeInfo
    register(TypeDescriptor.TypeInfo, TypeInfo.SERIALIZER);
    register(TypeDescriptor.ComposedTypeInfo, ComposedTypeInfo.SERIALIZER);
    // .naming package
    register(TypeDescriptor.LocalName, LocalName.SERIALIZER);
    register(TypeDescriptor.GlobalName, GlobalName.SERIALIZER);
    // ZMI
    register(TypeDescriptor.ZoneManagementInfo, ZoneManagementInfo.SERIALIZER);
    // Gossips
    register(TypeDescriptor.ZonesUpdateGossip, ZonesUpdateGossip.SERIALIZER);
    register(TypeDescriptor.ZonesInterestGossip, ZonesInterestResponseGossip.SERIALIZER);
    register(TypeDescriptor.ZonesInterestInitialGossip, ZonesInterestInitialGossip.SERIALIZER);
    register(TypeDescriptor.SessionAcknowledgedGossip, SessionAcknowledgedGossip.SERIALIZER);
  }
}
