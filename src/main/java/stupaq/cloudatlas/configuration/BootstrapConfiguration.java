package stupaq.cloudatlas.configuration;

import com.google.common.base.Preconditions;

import stupaq.cloudatlas.messaging.MessageBus;
import stupaq.cloudatlas.threading.SingleThreadModel;
import stupaq.cloudatlas.threading.ThreadModel;

public class BootstrapConfiguration extends CAConfiguration {
  private final MessageBus bus;
  private final ThreadModel threadModel;

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

  public ThreadModel threadManager() {
    return threadModel;
  }

  public static class Builder {
    private CAConfiguration configuration = new CAConfiguration();
    private MessageBus bus = new MessageBus();
    private ThreadModel threadModel;

    public Builder configuration(CAConfiguration configuration) {
      this.configuration = configuration;
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
      return new BootstrapConfiguration(configuration, bus, threadModel);
    }
  }
}
