package stupaq.cloudatlas.services.scribe;

import com.google.common.util.concurrent.AbstractScheduledService;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;

import stupaq.cloudatlas.attribute.Attribute;
import stupaq.cloudatlas.configuration.BootstrapConfiguration;
import stupaq.cloudatlas.configuration.StartIfPresent;
import stupaq.cloudatlas.naming.EntityName;
import stupaq.cloudatlas.services.rmiserver.RMIServerConfigKeys;
import stupaq.cloudatlas.services.rmiserver.protocol.LocalClientProtocol;
import stupaq.cloudatlas.services.scribe.RecordsManager.Records;
import stupaq.commons.util.concurrent.FastStartScheduler;

import static stupaq.cloudatlas.services.rmiserver.RMIServer.createClient;

@StartIfPresent(section = "scribe")
public class AttributesScribe extends AbstractScheduledService
    implements AttributesScribeConfigKeys, RMIServerConfigKeys {
  private static final Log LOG = LogFactory.getLog(AttributesScribe.class);
  private final BootstrapConfiguration config;
  private final ScheduledExecutorService executor;
  private final RecordsManager recordsManager;
  private LocalClientProtocol client;

  public AttributesScribe(BootstrapConfiguration config) {
    this.config = config;
    this.executor = config.threadModel().singleThreaded(AttributesScribe.class);
    this.recordsManager = new RecordsManager(config);
  }

  @Override
  protected void startUp() throws NotBoundException, RemoteException {
    client = createClient(LocalClientProtocol.class, config);
  }

  @Override
  protected void runOneIteration() throws IOException {
    List<EntityName> entitiesList = config.getEntities(ENTITIES);
    if (!entitiesList.isEmpty()) {
      Iterator<Attribute> values = client.getValues(entitiesList).iterator();
      Iterator<EntityName> entities = entitiesList.iterator();
      long timestamp = config.clock().timestamp();
      while (entities.hasNext() && entities.hasNext()) {
        EntityName entity = entities.next();
        try (Records log = recordsManager.forEntity(entity)) {
          Attribute value = values.next();
          if (value != null) {
            log.record(timestamp, value.value());
          } else {
            LOG.warn("No value for entity: " + entity);
          }
        }
      }
    }
  }

  @Override
  protected Scheduler scheduler() {
    return new FastStartScheduler() {
      @Override
      protected long getNextDelayMs() throws Exception {
        return config.getLong(FETCH_INTERVAL, FETCH_INTERVAL_DEFAULT);
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
    config.threadModel().free(executor);
    client = null;
  }
}
