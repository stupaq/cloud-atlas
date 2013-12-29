package stupaq.cloudatlas.configuration;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;

import org.apache.commons.configuration.BaseConfiguration;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.DataConfiguration;

import java.util.ArrayList;
import java.util.List;

import stupaq.cloudatlas.attribute.values.CAContact;
import stupaq.cloudatlas.naming.EntityName;
import stupaq.cloudatlas.naming.GlobalName;
import stupaq.cloudatlas.plugins.PluginLoader;
import stupaq.cloudatlas.services.collector.AttributesCollectorConfigKeys;
import stupaq.cloudatlas.services.zonemanager.ZoneManagerConfigKeys;

public class CAConfiguration extends DataConfiguration {
  public CAConfiguration() {
    this(new BaseConfiguration());
  }

  public CAConfiguration(Configuration configuration) {
    super(configuration);
  }

  @Override
  public CAConfiguration subset(String prefix) {
    return new CAConfiguration(super.subset(prefix));
  }

  public List<EntityName> getEntities(String key) {
    List<EntityName> entities = new ArrayList<>();
    for (String str : getStringArray(key)) {
      entities.add(EntityName.parse(str));
    }
    return entities;
  }

  public Optional<List<GlobalName>> getGlobalNames(String key) {
    if (!containsKey(key)) {
      return Optional.absent();
    }
    List<GlobalName> names = new ArrayList<>();
    for (String str : getStringArray(key)) {
      names.add(GlobalName.parse(str));
    }
    return Optional.of(names);
  }

  public GlobalName getGlobalName(String key) {
    return GlobalName.parse(getString(key));
  }

  public <Contract> Class<Contract> getPlugin(String key, String bundle, Class<Contract> fallback) {
    return containsKey(key) ? PluginLoader.<Contract>forName(bundle + getString(key)) : fallback;
  }

  public CAContact getLocalContact(int port) {
    return new CAContact("127.0.0.1:" + port);
  }

  public void mustContain(String key) {
    Preconditions.checkState(containsKey(key), "Configuration must contain value for key: " + key);
  }

  public String findCharacteristicZone() {
    if (containsKey(ZoneManagerConfigKeys.ZONE_NAME)) {
      return getString(ZoneManagerConfigKeys.ZONE_NAME);
    }
    if (containsKey(AttributesCollectorConfigKeys.ZONE_NAME)) {
      return getString(AttributesCollectorConfigKeys.ZONE_NAME);
    }
    throw new IllegalArgumentException("Could not find any promising attribute");
  }
}
