package stupaq.cloudatlas.configuration;

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

import stupaq.cloudatlas.services.scribe.Entity;

public class CAConfiguration extends DataConfiguration {
  private static final Log LOG = LogFactory.getLog(CAConfiguration.class);

  public CAConfiguration() {
    this(new BaseConfiguration());
  }

  public CAConfiguration(Configuration configuration) {
    super(configuration);
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

  public List<Entity> getEntities(String key) {
    List<Entity> entities = new ArrayList<>();
    for (String str : getStringArray(key)) {
      entities.add(Entity.parse(str));
    }
    return entities;
  }
}
