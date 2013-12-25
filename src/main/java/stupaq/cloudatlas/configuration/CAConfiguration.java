package stupaq.cloudatlas.configuration;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;

import org.apache.commons.configuration.BaseConfiguration;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.DataConfiguration;

import java.util.ArrayList;
import java.util.List;

import stupaq.cloudatlas.naming.EntityName;
import stupaq.cloudatlas.naming.GlobalName;
import stupaq.cloudatlas.plugins.Plugin;

public class CAConfiguration extends DataConfiguration {
  public CAConfiguration() {
    this(new BaseConfiguration());
  }

  public CAConfiguration(Configuration configuration) {
    super(configuration);
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

  public <Contract> Class<Contract> getPlugin(String key, Class<Contract> fallback) {
    return containsKey(key) ? Plugin.<Contract>forName(getString(key)) : fallback;
  }

  public void mustContain(String key) {
    Preconditions.checkState(containsKey(key), "Configuration must contain value for key: " + key);
  }
}
