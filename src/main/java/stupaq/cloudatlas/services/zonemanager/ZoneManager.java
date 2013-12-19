package stupaq.cloudatlas.services.zonemanager;

import com.google.common.base.Preconditions;
import com.google.common.eventbus.Subscribe;
import com.google.common.util.concurrent.AbstractScheduledService;

import org.apache.commons.configuration.Configuration;

import java.util.Collections;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import stupaq.cloudatlas.messaging.MessageBus;
import stupaq.cloudatlas.messaging.MessageListener;
import stupaq.cloudatlas.messaging.MessageListener.AbstractMessageListener;
import stupaq.cloudatlas.messaging.messages.AttributesUpdateMessage;
import stupaq.cloudatlas.messaging.messages.DumpZoneRequest;
import stupaq.cloudatlas.messaging.messages.DumpZoneResponse;
import stupaq.cloudatlas.messaging.messages.KnownZonesRequest;
import stupaq.cloudatlas.messaging.messages.KnownZonesResponse;
import stupaq.cloudatlas.naming.GlobalName;
import stupaq.cloudatlas.naming.LocalName;
import stupaq.cloudatlas.services.zonemanager.hierarchy.ZoneHierarchy;
import stupaq.cloudatlas.services.zonemanager.hierarchy.ZoneHierarchy.Inserter;
import stupaq.commons.util.concurrent.AsynchronousInvoker.ScheduledInvocation;
import stupaq.commons.util.concurrent.SingleThreadedExecutor;

public class ZoneManager extends AbstractScheduledService implements ZoneManagerConfigKeys {
  private final Configuration configuration;
  private final MessageBus bus;
  private final GlobalName agentsName;
  private final ZoneHierarchy<ZoneManagementInfo> hierarchy;
  private final SingleThreadedExecutor executor = new SingleThreadedExecutor();
  private final ZoneManagementInfo agentsZmi;

  public ZoneManager(Configuration configuration, MessageBus bus, GlobalName agentsName) {
    this.configuration = configuration;
    this.bus = bus;
    this.agentsName = agentsName;
    hierarchy = new ZoneHierarchy<>(new ZoneManagementInfo(LocalName.getRoot()));
    agentsZmi = hierarchy.insert(agentsName, new Inserter<ZoneManagementInfo>() {
      @Override
      public ZoneManagementInfo create(LocalName local) {
        return new ZoneManagementInfo(local);
      }
    });
  }

  @Override
  protected void startUp() throws Exception {
    // We're ready to operate
    bus.register(new ZoneManagerListener());
  }

  @Override
  protected void shutDown() throws Exception {
    executor.shutdown();
  }

  @Override
  protected ScheduledExecutorService executor() {
    return executor;
  }

  @Override
  protected void runOneIteration() throws Exception {
    // FIXME recompute parameters
    // FIXME update timestamps
  }

  @Override
  protected Scheduler scheduler() {
    return Scheduler.newFixedDelaySchedule(0,
        configuration.getLong(REEVALUATION_INTERVAL, REEVALUATION_INTERVAL_DEFAULT),
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
    public void knownZones(KnownZonesRequest request);
  }

  private class ZoneManagerListener extends AbstractMessageListener implements ZoneManagerContract {
    protected ZoneManagerListener() {
      super(executor, ZoneManagerContract.class);
    }

    @Override
    public void updateAttributes(AttributesUpdateMessage update) {
      Preconditions.checkArgument(agentsName.equals(update.getZone()));
      // FIXME
    }

    @Override
    public void dumpZone(DumpZoneRequest request) {
      bus.post(new DumpZoneResponse(agentsZmi.export()));
    }

    @Override
    public void knownZones(KnownZonesRequest request) {
      // FIXME
      bus.post(new KnownZonesResponse(Collections.<GlobalName>emptyList()).attach(request));
    }
  }
}
