package stupaq.cloudatlas.configuration;

import stupaq.cloudatlas.messaging.MessageBus;
import stupaq.cloudatlas.naming.GlobalName;
import stupaq.cloudatlas.threading.ThreadManager;

public class BootstrapConfiguration extends CAConfiguration {
  private final GlobalName leafZone;
  private final MessageBus bus;
  private final ThreadManager threadManager;

  public BootstrapConfiguration(CAConfiguration configuration, GlobalName leafZone, MessageBus bus,
      ThreadManager threadManager) {
    super(configuration);
    this.leafZone = leafZone;
    this.bus = bus;
    this.threadManager = threadManager;
  }

  public GlobalName getLeafZone() {
    return leafZone;
  }

  public MessageBus getBus() {
    return bus;
  }

  public ThreadManager threadManager() {
    return threadManager;
  }

  public static class Builder {
    private CAConfiguration configuration = new CAConfiguration();
    private GlobalName leafZone;
    private MessageBus bus = new MessageBus();
    private ThreadManager threadManager;

    public Builder configuration(CAConfiguration configuration) {
      this.configuration = configuration;
      return this;
    }

    public Builder leafZone(GlobalName leafZone) {
      this.leafZone = leafZone;
      return this;
    }

    public Builder bus(MessageBus bus) {
      this.bus = bus;
      return this;
    }

    public Builder threadManager(ThreadManager threadManager) {
      this.threadManager = threadManager;
      return this;
    }

    public BootstrapConfiguration create() {
      if (threadManager == null) {
        threadManager = new ThreadManager(configuration);
      }
      return new BootstrapConfiguration(configuration, leafZone, bus, threadManager);
    }
  }
}
