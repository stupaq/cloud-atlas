package stupaq.cloudatlas.configuration;

import com.google.common.base.Preconditions;

import stupaq.cloudatlas.messaging.MessageBus;
import stupaq.cloudatlas.naming.GlobalName;
import stupaq.cloudatlas.threading.PerServiceThreadModel;
import stupaq.cloudatlas.threading.ThreadModel;

public class BootstrapConfiguration extends CAConfiguration {
  private final GlobalName leafZone;
  private final MessageBus bus;
  private final ThreadModel threadModel;

  public BootstrapConfiguration(CAConfiguration configuration, GlobalName leafZone, MessageBus bus,
      ThreadModel threadModel) {
    super(configuration);
    Preconditions.checkNotNull(bus);
    Preconditions.checkNotNull(threadModel);
    this.leafZone = leafZone;
    this.bus = bus;
    this.threadModel = threadModel;
  }

  public GlobalName getLeafZone() {
    return leafZone;
  }

  public MessageBus getBus() {
    return bus;
  }

  public ThreadModel threadManager() {
    return threadModel;
  }

  public static class Builder {
    private CAConfiguration configuration = new CAConfiguration();
    private GlobalName leafZone;
    private MessageBus bus = new MessageBus();
    private ThreadModel threadModel;

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

    public Builder threadModel(ThreadModel threadModel) {
      this.threadModel = threadModel;
      return this;
    }

    public BootstrapConfiguration create() {
      if (threadModel == null) {
        threadModel = new PerServiceThreadModel();
      }
      return new BootstrapConfiguration(configuration, leafZone, bus, threadModel);
    }
  }
}
