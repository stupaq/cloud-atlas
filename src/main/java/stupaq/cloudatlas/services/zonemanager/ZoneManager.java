package stupaq.cloudatlas.services.zonemanager;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
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
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import stupaq.cloudatlas.attribute.Attribute;
import stupaq.cloudatlas.attribute.values.CAContact;
import stupaq.cloudatlas.attribute.values.CAQuery;
import stupaq.cloudatlas.attribute.values.CATime;
import stupaq.cloudatlas.configuration.BootstrapConfiguration;
import stupaq.cloudatlas.messaging.MessageBus;
import stupaq.cloudatlas.messaging.MessageListener;
import stupaq.cloudatlas.messaging.MessageListener.AbstractMessageListener;
import stupaq.cloudatlas.messaging.messages.AttributesUpdateMessage;
import stupaq.cloudatlas.messaging.messages.ContactSelectionMessage;
import stupaq.cloudatlas.messaging.messages.DuplexGossipingMessage;
import stupaq.cloudatlas.messaging.messages.FallbackContactsMessage;
import stupaq.cloudatlas.messaging.messages.QueryRemovalMessage;
import stupaq.cloudatlas.messaging.messages.QueryUpdateMessage;
import stupaq.cloudatlas.messaging.messages.gossips.AbstractZonesInterestGossip;
import stupaq.cloudatlas.messaging.messages.gossips.OutboundGossip;
import stupaq.cloudatlas.messaging.messages.gossips.SessionAcknowledgedGossip;
import stupaq.cloudatlas.messaging.messages.gossips.ZonesInterestInitialGossip;
import stupaq.cloudatlas.messaging.messages.gossips.ZonesInterestResponseGossip;
import stupaq.cloudatlas.messaging.messages.gossips.ZonesUpdateGossip;
import stupaq.cloudatlas.messaging.messages.requests.DumpZoneRequest;
import stupaq.cloudatlas.messaging.messages.requests.EntitiesValuesRequest;
import stupaq.cloudatlas.messaging.messages.requests.KnownZonesRequest;
import stupaq.cloudatlas.messaging.messages.responses.DumpZoneResponse;
import stupaq.cloudatlas.messaging.messages.responses.EntitiesValuesResponse;
import stupaq.cloudatlas.messaging.messages.responses.KnownZonesResponse;
import stupaq.cloudatlas.naming.AttributeName;
import stupaq.cloudatlas.naming.EntityName;
import stupaq.cloudatlas.naming.GlobalName;
import stupaq.cloudatlas.naming.LocalName;
import stupaq.cloudatlas.query.typecheck.TypeInfo;
import stupaq.cloudatlas.services.busybody.BusybodyConfigKeys;
import stupaq.cloudatlas.services.busybody.sessions.SessionId;
import stupaq.cloudatlas.services.zonemanager.builtins.BuiltinAttribute;
import stupaq.cloudatlas.services.zonemanager.builtins.BuiltinAttributesConfigKeys;
import stupaq.cloudatlas.services.zonemanager.builtins.BuiltinsInserter;
import stupaq.cloudatlas.services.zonemanager.builtins.BuiltinsUpdater;
import stupaq.cloudatlas.services.zonemanager.hierarchy.ZoneHierarchy;
import stupaq.cloudatlas.services.zonemanager.hierarchy.ZoneHierarchy.InPlaceModifier;
import stupaq.cloudatlas.services.zonemanager.hierarchy.ZoneHierarchy.InPlaceSynthesizer;
import stupaq.cloudatlas.services.zonemanager.hierarchy.ZoneHierarchy.Modifier;
import stupaq.cloudatlas.services.zonemanager.purging.UnexpiredZonesFilter;
import stupaq.cloudatlas.services.zonemanager.query.InstalledQueriesUpdater;
import stupaq.commons.util.concurrent.AsynchronousInvoker.ScheduledInvocation;
import stupaq.commons.util.concurrent.SingleThreadAssertion;
import stupaq.commons.util.concurrent.SingleThreadedExecutor;

public class ZoneManager extends AbstractScheduledService
    implements ZoneManagerConfigKeys, BuiltinAttributesConfigKeys, BusybodyConfigKeys {
  private static final Log LOG = LogFactory.getLog(ZoneManager.class);
  private final SingleThreadAssertion assertion = new SingleThreadAssertion();
  private final BootstrapConfiguration config;
  private final MessageBus bus;
  private final GlobalName agentsName;
  private final ZoneHierarchy<ZoneManagementInfo> hierarchy;
  private final SingleThreadedExecutor executor;
  private final ZoneHierarchy<ZoneManagementInfo> agentsNode;
  private final Set<CAContact> fallbackContacts = Sets.newHashSet();
  private MappedByteBuffer hierarchyDump;

  public ZoneManager(BootstrapConfiguration config) {
    this.config = config;
    bus = config.bus();
    agentsName = config.getGlobalName(ZONE_NAME);
    hierarchy = ZoneHierarchy.create(agentsName, new BuiltinsInserter(config));
    agentsNode = hierarchy.find(agentsName).get();
    executor = config.threadModel().singleThreaded(ZoneManager.class);
  }

  @Override
  protected void startUp() {
    // Create memory mapped file for zone hierarchy dumps
    if (config.containsKey(HIERARCHY_DUMP_FILE)) {
      try {
        String filename = config.getString(HIERARCHY_DUMP_FILE);
        if (filename.endsWith(GlobalName.SEPARATOR)) {
          filename += agentsName.toString();
        }
        File dumpFile = new File(filename);
        if (!dumpFile.delete()) {
          Files.createParentDirs(dumpFile);
        }
        hierarchyDump = Files.map(dumpFile, MapMode.READ_WRITE,
            config.getLong(HIERARCHY_DUMP_SIZE, HIERARCHY_DUMP_SIZE_DEFAULT));
      } catch (IOException e) {
        LOG.error("Failed to open file for hierarchy dumps", e);
      }
    }
    // We need to populate all attributes that are expected to exist
    agentsNode.synthesizePath(new InstalledQueriesUpdater());
    agentsNode.synthesizePath(new BuiltinsUpdater(config.clock()));
    // We're ready to operate
    bus.register(new ZoneManagerListener());
  }

  @Override
  protected void shutDown() {
    config.threadModel().free(executor);
  }

  @Override
  protected ScheduledExecutorService executor() {
    return executor;
  }

  @Override
  protected void runOneIteration() throws Exception {
    assertion.check();
    // We do the computation and updates for zones that we are a source of truth ONLY
    agentsNode.synthesizePath(new InstalledQueriesUpdater());
    agentsNode.synthesizePath(new BuiltinsUpdater(config.clock()));
    hierarchy.filterLeaves(new UnexpiredZonesFilter(config.clock(), config));
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

    @Subscribe
    @ScheduledInvocation
    public void setFallbackContacts(FallbackContactsMessage message);

    @Subscribe
    @ScheduledInvocation
    public void selectContact(ContactSelectionMessage message);

    @Subscribe
    @ScheduledInvocation
    public void duplexCommunication(DuplexGossipingMessage message);

    @Subscribe
    @ScheduledInvocation
    public void exportZones(AbstractZonesInterestGossip message);

    @Subscribe
    @ScheduledInvocation
    public void updateZones(ZonesUpdateGossip message);
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
        if (BuiltinAttribute.isWriteProtected(attribute.name())) {
          LOG.warn("Attempt to update write-protected attribute: " + attribute.name());
          continue;
        }
        agentsNode.payload().setPrime(attribute);
      }
    }

    @Override
    public void dumpZone(DumpZoneRequest request) {
      assertion.check();
      bus.post(new DumpZoneResponse(agentsNode.payload().export()));
    }

    @Override
    public void dumpValues(EntitiesValuesRequest request) {
      assertion.check();
      List<Attribute> attributes = new ArrayList<>();
      for (EntityName entity : request) {
        Optional<ZoneManagementInfo> zmi = hierarchy.findPayload(entity.zone);
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
      bus.post(new KnownZonesResponse(hierarchy.map(new Function<ZoneManagementInfo, LocalName>() {
        @Override
        public LocalName apply(ZoneManagementInfo zmi) {
          return zmi.localName();
        }
      })).attach(request));
    }

    @Override
    public void updateQuery(final QueryUpdateMessage message) {
      // First remove this query from all zones
      removeQuery(new QueryRemovalMessage(Optional.of(message.getQuery().name()),
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
            zmi.removeOfType(TypeInfo.of(CAQuery.class));
          }
        }
      }, true);
    }

    @Override
    public void setFallbackContacts(FallbackContactsMessage message) {
      fallbackContacts.clear();
      Iterables.addAll(fallbackContacts, message);
    }

    private void iterateZMIs(Optional<List<GlobalName>> names,
        final Modifier<ZoneManagementInfo> action, final boolean noLeaves) {
      if (names.isPresent()) {
        for (GlobalName name : names.get()) {
          Optional<ZoneHierarchy<ZoneManagementInfo>> zone = hierarchy.find(name);
          if (zone.isPresent()) {
            if (!noLeaves || !zone.get().isLeaf()) {
              action.apply(zone.get().payload());
            }
          } else {
            LOG.warn("Specified zone: " + name + " does not exist");
          }
        }
      } else {
        agentsNode.synthesizePath(new InPlaceSynthesizer<ZoneManagementInfo>() {
          @Override
          protected void process(Iterable<ZoneManagementInfo> children, ZoneManagementInfo zmi) {
            if (!noLeaves || !Iterables.isEmpty(children)) {
              action.apply(zmi);
            }
          }
        });
      }
    }

    @Override
    public void selectContact(ContactSelectionMessage message) {
      CAContact contact;
      try {
        Optional<CAContact> choice = message.getStrategy().select(agentsNode, fallbackContacts);
        if (!choice.isPresent()) {
          LOG.error("Could not find contact, aborting gossiping round");
          return;
        }
        contact = choice.get();
      } catch (Exception e) {
        LOG.error("Contact selection failed, aborting gossiping round", e);
        return;
      }
      LOG.debug("Selected contact: " + contact);
      SessionId session = message.getSessionId();
      bus.post(new OutboundGossip(contact,
          new ZonesInterestInitialGossip(agentsName, prepareKnownZones()).initiates(session)));
    }

    @Override
    public void duplexCommunication(DuplexGossipingMessage message) {
      SessionId session = message.getSessionId();
      if (LOG.isTraceEnabled()) {
        LOG.trace(
            "Duplex communication with: " + message.getContact() + " using session: " + session);
      }
      bus.post(new OutboundGossip(message.getContact(),
          new ZonesInterestResponseGossip(agentsName, prepareKnownZones()).initiates(session)));
    }

    private Map<GlobalName, CATime> prepareKnownZones() {
      Map<GlobalName, CATime> knownZones = Maps.newHashMap();
      // Zones in a single agent form a single spine
      ZoneHierarchy<ZoneManagementInfo> current = agentsNode.parent();
      for (; current != null; current = current.parent()) {
        for (ZoneHierarchy<ZoneManagementInfo> sibling : current.children()) {
          knownZones.put(sibling.globalName(), TIMESTAMP.get(sibling.payload()));
        }
      }
      return knownZones;
    }

    @Override
    public void exportZones(AbstractZonesInterestGossip message) {
      GlobalName otherName = message.getLeaf();
      GlobalName lca = agentsName.lca(otherName);
      LOG.info("Agent: " + otherName + " requested zone updates");
      Map<GlobalName, ZoneManagementInfo> updates = Maps.newHashMap();
      ZoneHierarchy<ZoneManagementInfo> current = hierarchy.find(lca).get();
      for (; current != null; current = current.parent()) {
        for (ZoneHierarchy<ZoneManagementInfo> sibling : current.children()) {
          GlobalName name = sibling.globalName();
          if (name.ancestor(otherName)) {
            // Ancestor ZMIs will be recomputed by communicating agent
            continue;
          }
          CATime knownTime = message.getTimestamp(name);
          ZoneManagementInfo zmi = sibling.payload();
          if (zmi.isNewerThan(knownTime).getOr(true)) {
            // Known timestamp is older or does not exist
            ZoneManagementInfo exported = zmi.export();
            // This is not very fast, however I do not want to break design or do some
            // cryptic partial serialization and we will eventually distribute
            // queries/certificates through gossiping.
            exported.removeOfType(TypeInfo.of(CAQuery.class));
            updates.put(name, exported);
          }
        }
      }
      bus.post(
          new OutboundGossip(message.sender(), new ZonesUpdateGossip(updates).respondsTo(message)));
    }

    @Override
    public void updateZones(ZonesUpdateGossip message) {
      // Got it! Whatever happens next we can respond with acknowledgement.
      bus.post(new OutboundGossip(message.sender(),
          new SessionAcknowledgedGossip().respondsTo(message)));
      // We will reject zones that are too old and to be purged in the next iteration
      UnexpiredZonesFilter filter = new UnexpiredZonesFilter(config.clock(), config);
      for (Entry<GlobalName, ZoneManagementInfo> entry : message) {
        GlobalName name = entry.getKey();
        ZoneManagementInfo update = entry.getValue();
        if (filter.apply(update)) {
          if (name.ancestor(agentsName)) {
            // Ancestor ZMIs will be recomputed by us
            LOG.warn("This ZMI update: " + name + " will be ignored by: " + agentsNode);
            continue;
          }
          Optional<ZoneManagementInfo> zone = hierarchy.findPayload(name);
          if (zone.isPresent()) {
            ZoneManagementInfo known = zone.get();
            // We make sure that this update actually makes sense, this way we don't care for
            // how long the packet has been roaming in the network and we do not care about
            // long-lasting gossiping sessions
            if (known.update(update)) {
              LOG.info("Updated zone info: " + name);
            } else {
              if (LOG.isWarnEnabled()) {
                LOG.warn("Aborted update: " + name + " no newer timestamp: known: " +
                    TIMESTAMP.get(known) + " update: " + TIMESTAMP.get(update));
              }
            }
            continue;
          }
          Optional<ZoneHierarchy<ZoneManagementInfo>> parent = hierarchy.find(name.parent());
          if (parent.isPresent()) {
            new ZoneHierarchy<>(update).attachTo(parent.get());
            LOG.info("Received new zone: " + name);
            continue;
          }
          LOG.warn("Received unwanted zone update: " + name);
        } else {
          LOG.warn("Rejected too old update for zone: " + name);
        }
      }
    }
  }
}
