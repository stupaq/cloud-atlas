package stupaq.cloudatlas.services.scribe;

import com.google.common.util.concurrent.AbstractScheduledService;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import stupaq.cloudatlas.attribute.AttributeValue;
import stupaq.cloudatlas.configuration.CAConfiguration;
import stupaq.cloudatlas.messaging.messages.EntitiesValuesRequest;
import stupaq.cloudatlas.naming.EntityName;
import stupaq.cloudatlas.services.rmiserver.protocol.LocalClientProtocol;
import stupaq.cloudatlas.services.scribe.RecordsManager.Records;
import stupaq.cloudatlas.time.Clock;

import static stupaq.compact.SerializableWrapper.wrap;

public class AttributesScribe extends AbstractScheduledService
    implements AttributesScribeConfigKeys {
  private final CAConfiguration configuration;
  private final LocalClientProtocol client;
  private final ScheduledExecutorService executor;
  private final Clock clock = new Clock();
  private final RecordsManager recordsManager;

  public AttributesScribe(CAConfiguration configuration, LocalClientProtocol client,
      ScheduledExecutorService executor) {
    this.configuration = configuration;
    this.client = client;
    this.executor = executor;
    this.recordsManager = new RecordsManager(configuration);
  }

  @Override
  protected void runOneIteration() throws IOException {
    List<EntityName> entitiesList = configuration.getEntities(ENTITIES);
    Iterator<AttributeValue> values =
        client.getValues(wrap(new EntitiesValuesRequest(entitiesList))).get().iterator();
    Iterator<EntityName> entities = entitiesList.iterator();
    long timestamp = clock.getTime();
    while (entities.hasNext() && entities.hasNext()) {
      try (Records log = recordsManager.forEntity(entities.next())) {
        log.record(timestamp, values.next());
      }
    }
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

  @Override
  protected void shutDown() throws Exception {
    recordsManager.close();
  }
}
