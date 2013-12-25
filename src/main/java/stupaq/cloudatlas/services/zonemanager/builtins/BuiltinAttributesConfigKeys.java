package stupaq.cloudatlas.services.zonemanager.builtins;

import stupaq.cloudatlas.attribute.values.CAContact;
import stupaq.cloudatlas.attribute.values.CAInteger;
import stupaq.cloudatlas.attribute.values.CASet;
import stupaq.cloudatlas.attribute.values.CAString;
import stupaq.cloudatlas.attribute.values.CATime;
import stupaq.cloudatlas.query.typecheck.ComposedTypeInfo;

import static stupaq.cloudatlas.query.typecheck.TypeInfo.of;
import static stupaq.cloudatlas.services.zonemanager.builtins.BuiltinAttribute.create;

@SuppressWarnings("unchecked")
public interface BuiltinAttributesConfigKeys {
  // Static configuration
  static final BuiltinAttribute<CAInteger> LEVEL = create("level", of(CAInteger.class));
  static final BuiltinAttribute<CAString> NAME = create("name", of(CAString.class));
  static final BuiltinAttribute<CAString> OWNER = create("owner", of(CAString.class));
  static final BuiltinAttribute<CASet<CAContact>> CONTACTS =
      create("contacts", ComposedTypeInfo.of(CASet.class, of(CAContact.class)));
  static final BuiltinAttribute<CATime> TIMESTAMP = create("timestamp", of(CATime.class));
  static final BuiltinAttribute<CAInteger> CARDINALITY = create("cardinality", of(CAInteger.class));
}
