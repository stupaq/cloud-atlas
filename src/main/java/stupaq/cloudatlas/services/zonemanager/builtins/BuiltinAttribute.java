package stupaq.cloudatlas.services.zonemanager.builtins;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.collect.Sets;

import java.util.Set;

import stupaq.cloudatlas.attribute.Attribute;
import stupaq.cloudatlas.attribute.AttributeValue;
import stupaq.cloudatlas.naming.AttributeName;
import stupaq.cloudatlas.query.typecheck.TypeInfo;
import stupaq.cloudatlas.services.zonemanager.ZoneManagementInfo;

public class BuiltinAttribute<Type extends AttributeValue> {
  private static final Set<AttributeName> KNOWN_NAMES = Sets.newConcurrentHashSet();
  private final AttributeName name;
  private final TypeInfo<Type> type;

  protected BuiltinAttribute(AttributeName name, TypeInfo<Type> type) {
    this.name = name;
    this.type = type;
  }

  public Attribute<Type> get(ZoneManagementInfo zmi) {
    Optional<Attribute<Type>> value = zmi.get(name, type);
    Preconditions.checkState(value.isPresent());
    return value.get();
  }

  public Attribute<Type> create(Type value) {
    Preconditions.checkArgument(type.matches(value));
    return new Attribute<>(name, value);
  }

  public AttributeName getName() {
    return name;
  }

  public static <Type extends AttributeValue> BuiltinAttribute create(String name,
      TypeInfo<Type> type) {
    BuiltinAttribute<Type> attribute = new BuiltinAttribute<>(AttributeName.fromString(name), type);
    KNOWN_NAMES.add(attribute.getName());
    return attribute;
  }

  public static boolean isBuiltin(AttributeName name) {
    return KNOWN_NAMES.contains(name);
  }
}
