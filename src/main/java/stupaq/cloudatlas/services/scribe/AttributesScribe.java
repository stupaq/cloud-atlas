package stupaq.cloudatlas.services.scribe;

import com.google.common.util.concurrent.AbstractScheduledService;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import stupaq.cloudatlas.attribute.Attribute;
import stupaq.cloudatlas.configuration.BootstrapConfiguration;
import stupaq.cloudatlas.naming.EntityName;
import stupaq.cloudatlas.services.rmiserver.protocol.LocalClientProtocol;
import stupaq.cloudatlas.services.scribe.RecordsManager.Records;
import stupaq.cloudatlas.time.Clock;

public class AttributesScribe extends AbstractScheduledService
    implements AttributesScribeConfigKeys {
  private static final Log LOG = LogFactory.getLog(AttributesScribe.class);
  private final BootstrapConfiguration config;
  private final LocalClientProtocol client;
  private final ScheduledExecutorService executor;
  private final RecordsManager recordsManager;
  private final Clock clock = new Clock();

  public AttributesScribe(BootstrapConfiguration config, LocalClientProtocol client) {
    this.config = config;
    this.client = client;
    this.executor = config.threadManager().singleThreaded(AttributesScribe.class);
    this.recordsManager = new RecordsManager(config);
  }

  @Override
  protected void runOneIteration() throws IOException {
    List<EntityName> entitiesList = config.getEntities(ENTITIES);
    if (LOG.isDebugEnabled()) {
      LOG.debug("Asking for entities: " + entitiesList);
    }
    Iterator<Attribute> values = client.getValues(entitiesList).iterator();
    Iterator<EntityName> entities = entitiesList.iterator();
    long timestamp = clock.getTime();
    while (entities.hasNext() && entities.hasNext()) {
      EntityName entity = entities.next();
      try (Records log = recordsManager.forEntity(entity)) {
        Attribute value = values.next();
        if (value != null) {
          log.record(timestamp, value.getValue());
        }
      }
    }
  }

  @Override
  protected Scheduler scheduler() {
    return new CustomScheduler() {
      @Override
      protected Schedule getNextSchedule() throws Exception {
        return new Schedule(config.getLong(FETCH_INTERVAL, FETCH_INTERVAL_DEFAULT),
            TimeUnit.MILLISECONDS);
      }
    };
  }

  @Override
  protected ScheduledExecutorService executor() {
    return executor;
  }

  @Override
  protected void shutDown() {
    recordsManager.close();
    config.threadManager().free(executor);
  }
}
