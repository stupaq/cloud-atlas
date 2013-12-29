package stupaq.cloudatlas.configuration;

import com.google.common.base.Preconditions;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.FileConfiguration;
import org.apache.commons.configuration.HierarchicalINIConfiguration;
import org.apache.commons.configuration.reloading.FileChangedReloadingStrategy;

import java.io.File;

import stupaq.cloudatlas.messaging.MessageBus;
import stupaq.cloudatlas.threading.SingleThreadModel;
import stupaq.cloudatlas.threading.ThreadModel;
import stupaq.cloudatlas.time.SynchronizedClock;

public class BootstrapConfiguration extends CAConfiguration {
  private final MessageBus bus;
  private final ThreadModel threadModel;
  private final SynchronizedClock clock = new SynchronizedClock();

  public BootstrapConfiguration(CAConfiguration configuration, MessageBus bus,
      ThreadModel threadModel) {
    super(configuration);
    Preconditions.checkNotNull(bus);
    Preconditions.checkNotNull(threadModel);
    this.bus = bus;
    this.threadModel = threadModel;
  }

  public MessageBus bus() {
    return bus;
  }

  public ThreadModel threadModel() {
    return threadModel;
  }

  public SynchronizedClock clock() {
    return clock;
  }

  public static class Builder {
    private static final String CONFIG_EXTENSION = ".ini";
    private CAConfiguration config = new CAConfiguration();
    private MessageBus bus = new MessageBus();
    private ThreadModel threadModel;

    public Builder config(CAConfiguration config) {
      this.config = config;
      return this;
    }

    public Builder configFile(File path, Class<?> clazz) throws ConfigurationException {
      if (path.isDirectory()) {
        path = new File(path, clazz.getSimpleName() + CONFIG_EXTENSION);
      }
      FileConfiguration config = new HierarchicalINIConfiguration(path);
      config.setReloadingStrategy(new FileChangedReloadingStrategy());
      this.config = new CAConfiguration(config);
      return this;
    }

    public Builder bus(MessageBus bus) {
      this.bus = bus;
      return this;
    }

    public Builder threadModel(ThreadModel threadModel) {
      this.threadModel = threadModel;
      return this;
    }

    public BootstrapConfiguration create() {
      if (threadModel == null) {
        threadModel = new SingleThreadModel();
      }
      return new BootstrapConfiguration(config, bus, threadModel);
    }
  }
}
