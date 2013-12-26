package stupaq.cloudatlas.plugins;

import stupaq.cloudatlas.configuration.CAConfiguration;

public final class PluginLoader {
  private PluginLoader() {
  }

  private static final String PREFIX = "plugin.";
  private static final String PACKAGE_PREFIX = PluginLoader.class.getPackage().getName() + ".";

  @SuppressWarnings("unchecked")
  public static <Contract> Class<Contract> forName(String name) {
    try {
      return (Class<Contract>) Class.forName(PACKAGE_PREFIX + name);
    } catch (ClassNotFoundException e) {
      throw new IllegalArgumentException(e);
    }
  }

  public static <Plugin> Plugin initialize(Class<? extends Plugin> clazz, CAConfiguration config) {
    try {
      return clazz.getConstructor(CAConfiguration.class).newInstance(config.subset(PREFIX));
    } catch (ReflectiveOperationException e) {
      throw new IllegalArgumentException(e);
    }
  }
}
