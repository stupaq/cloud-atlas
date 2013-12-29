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
import stupaq.cloudatlas.time.GTPSynchronizedClock;
import stupaq.cloudatlas.time.LocalClock;

public class BootstrapConfiguration extends CAConfiguration {
  private final MessageBus bus;
  private final ThreadModel threadModel;
  private final GTPSynchronizedClock synchronizedClock;

  public BootstrapConfiguration(CAConfiguration configuration, MessageBus bus,
      ThreadModel threadModel, GTPSynchronizedClock synchronizedClock) {
    super(configuration);
    Preconditions.checkNotNull(bus);
    Preconditions.checkNotNull(threadModel);
    Preconditions.checkNotNull(synchronizedClock);
    this.bus = bus;
    this.threadModel = threadModel;
    this.synchronizedClock = synchronizedClock;
  }

  public MessageBus bus() {
    return bus;
  }

  public ThreadModel threadModel() {
    return threadModel;
  }

  public LocalClock clock() {
    return synchronizedClock.localClock();
  }

  public GTPSynchronizedClock synchronizedClock() {
    return synchronizedClock;
  }

  public static class Builder {
    private static final String CONFIG_EXTENSION = ".ini";
    private final MessageBus bus = new MessageBus();
    private CAConfiguration config = new CAConfiguration();
    private ThreadModel threadModel;
    private GTPSynchronizedClock synchronizedClock;

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

    public Builder threadModel(ThreadModel threadModel) {
      this.threadModel = threadModel;
      return this;
    }

    public BootstrapConfiguration create() {
      if (threadModel == null) {
        threadModel = new SingleThreadModel();
      }
      if (synchronizedClock == null) {
        synchronizedClock = new GTPSynchronizedClock(config);
      }
      return new BootstrapConfiguration(config, bus, threadModel, synchronizedClock);
    }
  }
}
