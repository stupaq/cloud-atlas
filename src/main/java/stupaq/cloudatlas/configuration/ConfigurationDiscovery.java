package stupaq.cloudatlas.configuration;

import java.io.File;

import stupaq.cloudatlas.naming.GlobalName;
import stupaq.cloudatlas.runnable.agent.CAAgentProcess;
import stupaq.cloudatlas.runnable.client.CALocalClientProcess;

import static java.lang.System.getenv;

public class ConfigurationDiscovery implements EnvironmentConfigKeys {
  private ConfigurationDiscovery() {
  }

  private static final String CONFIG_EXTENSION = ".ini";
  private static final String CONTEXT_SEPARATOR = "-";

  public static String childName(Class<?> clazz) {
    return childName(clazz, null);
  }

  public static String childName(Class<?> clazz, String context) {
    return clazz.getSimpleName() + (context != null ? CONTEXT_SEPARATOR + context : "") +
        CONFIG_EXTENSION;
  }

  public static CAConfiguration forAgent(GlobalName leafZone) {
    return CAConfiguration.fromFile(new File(findDir(), childName(CAAgentProcess.class)));
  }

  public static CAConfiguration forLocalClient() {
    return CAConfiguration.fromFile(new File(findDir(), childName(CALocalClientProcess.class)));
  }

  private static File findDir() {
    return new File(getenv(CONFIG_DIR) == null ? CONFIG_DIR_DEFAULT : getenv(CONFIG_DIR));
  }
}
