package stupaq.cloudatlas.services.zonemanager;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.collect.Iterables;
import com.google.common.eventbus.Subscribe;
import com.google.common.io.Files;
import com.google.common.util.concurrent.AbstractScheduledService;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.File;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel.MapMode;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import stupaq.cloudatlas.attribute.Attribute;
import stupaq.cloudatlas.attribute.values.CAQuery;
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
import stupaq.cloudatlas.messaging.messages.QueryRemovalMessage;
import stupaq.cloudatlas.messaging.messages.QueryUpdateMessage;
import stupaq.cloudatlas.naming.AttributeName;
import stupaq.cloudatlas.naming.EntityName;
import stupaq.cloudatlas.naming.GlobalName;
import stupaq.cloudatlas.naming.LocalName;
import stupaq.cloudatlas.query.typecheck.TypeInfo;
import stupaq.cloudatlas.services.zonemanager.builtins.BuiltinsInserter;
import stupaq.cloudatlas.services.zonemanager.builtins.BuiltinsUpdater;
import stupaq.cloudatlas.services.zonemanager.hierarchy.ZoneHierarchy;
import stupaq.cloudatlas.services.zonemanager.hierarchy.ZoneHierarchy.InPlaceModifier;
import stupaq.cloudatlas.services.zonemanager.hierarchy.ZoneHierarchy.InPlaceSynthesizer;
import stupaq.cloudatlas.services.zonemanager.hierarchy.ZoneHierarchy.Modifier;
import stupaq.cloudatlas.services.zonemanager.query.InstalledQueriesUpdater;
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
  private final SingleThreadedExecutor executor;
  private final ZoneHierarchy<ZoneManagementInfo> agentsNode;
  private MappedByteBuffer hierarchyDump;

  public ZoneManager(BootstrapConfiguration config) {
    this.config = config;
    bus = config.bus();
    agentsName = config.getGlobalName(ZONE_NAME);
    hierarchy = ZoneHierarchy.create(agentsName, new BuiltinsInserter(agentsName));
    agentsNode = hierarchy.find(agentsName).get();
    executor = config.threadManager().singleThreaded(ZoneManager.class);
  }

  @Override
  protected void startUp() {
    // We're ready to operate
    bus.register(new ZoneManagerListener());
    // Create memory mapped file for zone hierarchy dumps
    if (config.containsKey(HIERARCHY_DUMP_FILE)) {
      try {
        File dumpFile = new File(config.getString(HIERARCHY_DUMP_FILE));
        if (!dumpFile.delete()) {
          Files.createParentDirs(dumpFile);
        }
        hierarchyDump = Files.map(dumpFile, MapMode.READ_WRITE,
            config.getLong(HIERARCHY_DUMP_SIZE, HIERARCHY_DUMP_SIZE_DEFAULT));
      } catch (IOException e) {
        LOG.error("Failed to open file for hierarchy dumps", e);
      }
    }
  }

  @Override
  protected void shutDown() {
    config.threadManager().free(executor);
  }

  @Override
  protected ScheduledExecutorService executor() {
    return executor;
  }

  @Override
  protected void runOneIteration() throws Exception {
    assertion.check();
    // We do the computation and updates for zones that we are a source of truth ONLY
    agentsNode.walkUp(new InstalledQueriesUpdater());
    agentsNode.walkUp(new BuiltinsUpdater(config.clock().getTime()));
    // TODO adjust timestamps
    dumpHierarchy();
  }

  private void dumpHierarchy() {
    if (hierarchyDump != null) {
      hierarchyDump.rewind();
      String dump = hierarchy.toString();
      int length = Math.min(hierarchyDump.remaining(), dump.length());
      hierarchyDump.put(dump.getBytes(), 0, length);
      hierarchyDump.put(new byte[hierarchyDump.remaining()]);
    }
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

    @Subscribe
    @ScheduledInvocation
    public void updateQuery(QueryUpdateMessage message);

    @Subscribe
    @ScheduledInvocation
    public void removeQuery(QueryRemovalMessage message);
  }

  private class ZoneManagerListener extends AbstractMessageListener implements ZoneManagerContract {
    protected ZoneManagerListener() {
      super(executor, ZoneManagerContract.class);
    }

    @Override
    public void updateAttributes(AttributesUpdateMessage update) {
      assertion.check();
      Preconditions.checkArgument(agentsName.equals(update.getZone()));
      for (Attribute attribute : update) {
        if (!BUILTIN_ATTRIBUTES.contains(attribute.getName())) {
          agentsNode.getPayload().setPrime(attribute);
        }
      }
    }

    @Override
    public void dumpZone(DumpZoneRequest request) {
      assertion.check();
      bus.post(new DumpZoneResponse(agentsNode.getPayload().export()));
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

    @Override
    public void updateQuery(final QueryUpdateMessage message) {
      // First remove this query from all zones
      removeQuery(new QueryRemovalMessage(Optional.of(message.getQuery().getName()),
          Optional.<List<GlobalName>>absent()));
      iterateZMIs(message.getZones(), new InPlaceModifier<ZoneManagementInfo>() {
        @Override
        public void process(ZoneManagementInfo zmi) {
          zmi.setPrime(message.getQuery());
        }
      }, true);
    }

    @Override
    public void removeQuery(QueryRemovalMessage message) {
      final Optional<AttributeName> name = message.getName();
      iterateZMIs(message.getZones(), new InPlaceModifier<ZoneManagementInfo>() {
        @Override
        public void process(ZoneManagementInfo zmi) {
          if (name.isPresent()) {
            zmi.remove(name.get());
          } else {
            // We have to materialize iterable here for future modifications
            for (Attribute attribute : zmi.specialAttributes().toList()) {
              if (TypeInfo.is(CAQuery.class, attribute.getValue())) {
                zmi.remove(attribute.getName());
              }
            }
          }
        }
      }, true);
    }

    private void iterateZMIs(Optional<List<GlobalName>> names,
        final Modifier<ZoneManagementInfo> action, final boolean noLeaves) {
      if (names.isPresent()) {
        for (GlobalName name : names.get()) {
          Optional<ZoneHierarchy<ZoneManagementInfo>> zone = hierarchy.find(name);
          if (zone.isPresent()) {
            if (!noLeaves || !zone.get().isLeaf()) {
              action.apply(zone.get().getPayload());
            }
          } else {
            LOG.warn("Specified zone: " + name + " does not exist");
          }
        }
      } else {
        agentsNode.walkUp(new InPlaceSynthesizer<ZoneManagementInfo>() {
          @Override
          protected void process(Iterable<ZoneManagementInfo> children, ZoneManagementInfo zmi) {
            if (!noLeaves || !Iterables.isEmpty(children)) {
              action.apply(zmi);
            }
          }
        });
      }
    }
  }
}
