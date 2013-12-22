package stupaq.cloudatlas.configuration;

import com.google.common.base.Preconditions;

import stupaq.cloudatlas.messaging.MessageBus;
import stupaq.cloudatlas.naming.GlobalName;
import stupaq.cloudatlas.threading.SingleThreadModel;
import stupaq.cloudatlas.threading.ThreadModel;

public class BootstrapConfiguration extends CAConfiguration {
  private final GlobalName zone;
  private final MessageBus bus;
  private final ThreadModel threadModel;

  public BootstrapConfiguration(CAConfiguration configuration, GlobalName zone, MessageBus bus,
      ThreadModel threadModel) {
    super(configuration);
    Preconditions.checkNotNull(bus);
    Preconditions.checkNotNull(threadModel);
    this.zone = zone;
    this.bus = bus;
    this.threadModel = threadModel;
  }

  public GlobalName zone() {
    return zone;
  }

  public MessageBus bus() {
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

    public Builder zone(GlobalName leafZone) {
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
        threadModel = new SingleThreadModel();
      }
      return new BootstrapConfiguration(configuration, leafZone, bus, threadModel);
    }
  }
}
