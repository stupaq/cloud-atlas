package stupaq.cloudatlas.services.busybody;

import com.google.common.util.concurrent.AbstractIdleService;

import java.util.concurrent.Executor;

import stupaq.cloudatlas.configuration.BootstrapConfiguration;
import stupaq.commons.util.concurrent.SingleThreadedExecutor;

public class Busybody extends AbstractIdleService implements BusybodyConfigKeys {
  private final BootstrapConfiguration config;
  private final SingleThreadedExecutor executor;

  public Busybody(BootstrapConfiguration config) {
    this.config = config;
    executor = config.threadManager().singleThreaded(Busybody.class);
  }

  @Override
  protected void startUp() {
  }

  @Override
  protected void shutDown() {
    config.threadManager().free(executor);
  }

  @Override
  protected Executor executor() {
    return executor;
  }
}
