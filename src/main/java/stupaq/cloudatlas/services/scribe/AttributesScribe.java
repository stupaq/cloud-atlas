package stupaq.cloudatlas.services.scribe;

import com.google.common.util.concurrent.AbstractScheduledService;

import sun.font.AttributeValues;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import stupaq.cloudatlas.configuration.CAConfiguration;
import stupaq.cloudatlas.services.rmiserver.protocol.LocalClientProtocol;
import stupaq.cloudatlas.time.Clock;
import stupaq.compact.SerializableWrapper;

public class AttributesScribe extends AbstractScheduledService
    implements AttributesScribeConfigKeys {
  private final CAConfiguration configuration;
  private final LocalClientProtocol client;
  private final ScheduledExecutorService executor;
  private final Clock clock = new Clock();

  public AttributesScribe(CAConfiguration configuration, LocalClientProtocol client,
      ScheduledExecutorService executor) {
    this.configuration = configuration;
    this.client = client;
    this.executor = executor;
  }

  @Override
  protected void runOneIteration() throws IOException {
    List<Entity> entities = configuration.getEntities(ENTITIES);
    // FIXME
  }

  @Override
  protected Scheduler scheduler() {
    return new CustomScheduler() {
      @Override
      protected Schedule getNextSchedule() throws Exception {
        return new Schedule(configuration.getLong(FETCH_INTERVAL, FETCH_INTERVAL_DEFAULT),
            TimeUnit.MILLISECONDS);
      }
    };
  }

  @Override
  protected ScheduledExecutorService executor() {
    return executor;
  }
}
