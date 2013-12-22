package stupaq.cloudatlas.configuration;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;

import org.apache.commons.configuration.BaseConfiguration;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.DataConfiguration;
import org.apache.commons.configuration.FileConfiguration;
import org.apache.commons.configuration.HierarchicalINIConfiguration;
import org.apache.commons.configuration.reloading.FileChangedReloadingStrategy;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import stupaq.cloudatlas.naming.EntityName;
import stupaq.cloudatlas.naming.GlobalName;

public class CAConfiguration extends DataConfiguration {
  private static final Log LOG = LogFactory.getLog(CAConfiguration.class);

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

  public void mustContain(String key) {
    Preconditions.checkState(containsKey(key), "Configuration must contain value for key: " + key);
  }

  public static CAConfiguration fromFile(File file) {
    try {
      FileConfiguration config = new HierarchicalINIConfiguration(file);
      config.setReloadingStrategy(new FileChangedReloadingStrategy());
      return new CAConfiguration(config);
    } catch (ConfigurationException e) {
      LOG.warn("Failed loading configuration, defaulting to empty one", e);
      return new CAConfiguration();
    }
  }
}
