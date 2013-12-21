package stupaq.cloudatlas.services.zonemanager;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.eventbus.Subscribe;
import com.google.common.util.concurrent.AbstractScheduledService;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import stupaq.cloudatlas.attribute.Attribute;
import stupaq.cloudatlas.configuration.BootstrapConfiguration;
import stupaq.cloudatlas.messaging.MessageBus;
import stupaq.cloudatlas.messaging.MessageListener;
import stupaq.cloudatlas.messaging.MessageListener.AbstractMessageListener;
import stupaq.cloudatlas.messaging.messages.AttributesUpdateMessage;
import stupaq.cloudatlas.messaging.messages.DumpZoneRequest;
import stupaq.cloudatlas.messaging.messages.DumpZoneResponse;
import stupaq.cloudatlas.messaging.messages.EntitiesValuesRequest;
import stupaq.cloudatlas.messaging.messages.EntitiesValuesResponse;
import stupaq.cloudatlas.messaging.messages.KnownZonesRequest;
import stupaq.cloudatlas.messaging.messages.KnownZonesResponse;
import stupaq.cloudatlas.naming.EntityName;
import stupaq.cloudatlas.naming.GlobalName;
import stupaq.cloudatlas.naming.LocalName;
import stupaq.cloudatlas.services.zonemanager.builtins.BuiltinsInserter;
import stupaq.cloudatlas.services.zonemanager.builtins.BuiltinsUpdater;
import stupaq.cloudatlas.services.zonemanager.hierarchy.ZoneHierarchy;
import stupaq.cloudatlas.services.zonemanager.query.InstalledQueriesUpdater;
import stupaq.cloudatlas.time.Clock;
import stupaq.commons.base.Function1;
import stupaq.commons.util.concurrent.AsynchronousInvoker.ScheduledInvocation;
import stupaq.commons.util.concurrent.SingleThreadAssertion;
import stupaq.commons.util.concurrent.SingleThreadedExecutor;

public class ZoneManager extends AbstractScheduledService implements ZoneManagerConfigKeys {
  private static final Log LOG = LogFactory.getLog(ZoneManager.class);
  private final SingleThreadAssertion assertion = new SingleThreadAssertion();
  private final BootstrapConfiguration config;
  private final MessageBus bus;
  private final GlobalName agentsName;
  private final ZoneHierarchy<ZoneManagementInfo> hierarchy;
  private final ZoneManagementInfo agentsZmi;
  private final SingleThreadedExecutor executor;
  private final Clock clock = new Clock();

  public ZoneManager(BootstrapConfiguration config) {
    this.config = config;
    this.bus = config.getBus();
    this.agentsName = config.getLeafZone();
    hierarchy = new ZoneHierarchy<>(new ZoneManagementInfo(LocalName.getRoot()));
    agentsZmi = hierarchy.insert(agentsName, new BuiltinsInserter(agentsName));
    Preconditions.checkState(hierarchy.getPayload(agentsName).get() == agentsZmi);
    executor = config.threadManager().singleThreaded(ZoneManager.class);
  }

  @Override
  protected void startUp() throws Exception {
    // We're ready to operate
    bus.register(new ZoneManagerListener());
  }

  @Override
  protected void shutDown() throws Exception {
    config.threadManager().free(executor);
  }

  @Override
  protected ScheduledExecutorService executor() {
    return executor;
  }

  @Override
  protected void runOneIteration() throws Exception {
    assertion.check();
    if (LOG.isDebugEnabled()) {
      LOG.debug("Zone hierarchy as seen by: " + agentsName + "\n" + hierarchy);
    }
    hierarchy.synthesize(new InstalledQueriesUpdater());
    hierarchy.synthesize(new BuiltinsUpdater(clock.getTime()));
    // TODO adjust timestamps
  }

  @Override
  protected Scheduler scheduler() {
    return Scheduler.newFixedDelaySchedule(0,
        config.getLong(REEVALUATION_INTERVAL, REEVALUATION_INTERVAL_DEFAULT),
        TimeUnit.MILLISECONDS);
  }

  private static interface ZoneManagerContract extends MessageListener {
    @Subscribe
    @ScheduledInvocation
    public void updateAttributes(AttributesUpdateMessage update);

    @Subscribe
    @ScheduledInvocation
    public void dumpZone(DumpZoneRequest request);

    @Subscribe
    @ScheduledInvocation
    public void dumpValues(EntitiesValuesRequest request);

    @Subscribe
    @ScheduledInvocation
    public void knownZones(KnownZonesRequest request);
  }

  private class ZoneManagerListener extends AbstractMessageListener implements ZoneManagerContract {
    protected ZoneManagerListener() {
      super(executor, ZoneManagerContract.class);
    }

    @Override
    public void updateAttributes(AttributesUpdateMessage update) {
      assertion.check();
      Preconditions.checkArgument(agentsName.equals(update.getZone()));
      if (update.isOverride()) {
        agentsZmi.clearPrime();
      }
      for (Attribute attribute : update) {
        agentsZmi.setPrime(attribute);
      }
    }

    @Override
    public void dumpZone(DumpZoneRequest request) {
      assertion.check();
      bus.post(new DumpZoneResponse(agentsZmi.export()));
    }

    @Override
    public void dumpValues(EntitiesValuesRequest request) {
      assertion.check();
      List<Attribute> attributes = new ArrayList<>();
      for (EntityName entity : request) {
        Optional<ZoneManagementInfo> zmi = hierarchy.getPayload(entity.zone);
        if (zmi.isPresent()) {
          Optional<Attribute> attribute = zmi.get().get(entity.attributeName);
          if (attribute.isPresent()) {
            attributes.add(attribute.get());
            LOG.debug("Asked for entity: " + entity + " found: " + attribute.get());
            continue;
          } else {
            LOG.debug("Attribute not found: " + entity.attributeName + " in zone: " + entity.zone);
          }
        } else {
          LOG.debug("Zone not found: " + entity.zone + " ");
        }
        attributes.add(null);
      }
      bus.post(new EntitiesValuesResponse(attributes).attach(request));
    }

    @Override
    public void knownZones(KnownZonesRequest request) {
      assertion.check();
      bus.post(new KnownZonesResponse(hierarchy.map(new Function1<ZoneManagementInfo, LocalName>() {
        @Override
        public LocalName apply(ZoneManagementInfo zoneManagementInfo) {
          return zoneManagementInfo.localName();
        }
      })).attach(request));
    }
  }
}
