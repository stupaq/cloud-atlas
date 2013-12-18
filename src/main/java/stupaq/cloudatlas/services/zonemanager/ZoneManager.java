package stupaq.cloudatlas.services.zonemanager;

import com.google.common.base.Preconditions;
import com.google.common.eventbus.Subscribe;
import com.google.common.util.concurrent.AbstractScheduledService;

import org.apache.commons.configuration.Configuration;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import stupaq.cloudatlas.messaging.MessageBus;
import stupaq.cloudatlas.messaging.MessageListener;
import stupaq.cloudatlas.messaging.MessageListener.AbstractMessageListener;
import stupaq.cloudatlas.messaging.messages.AttributesUpdateMessage;
import stupaq.cloudatlas.messaging.messages.DumpZoneRequest;
import stupaq.cloudatlas.messaging.messages.KnownZonesRequest;
import stupaq.cloudatlas.naming.GlobalName;
import stupaq.commons.util.concurrent.AsynchronousInvoker.ScheduledInvocation;
import stupaq.commons.util.concurrent.SingleThreadedExecutor;

public class ZoneManager extends AbstractScheduledService implements ZoneManagerConfigKeys {
  private final Configuration configuration;
  private final MessageBus bus;
  private final GlobalName agentsZone;
  private SingleThreadedExecutor executor;

  public ZoneManager(Configuration configuration, MessageBus bus, GlobalName agentsZone) {
    this.configuration = configuration;
    this.bus = bus;
    this.agentsZone = agentsZone;
  }

  @Override
  protected void startUp() throws Exception {
    executor = new SingleThreadedExecutor();
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
      Preconditions.checkArgument(agentsZone.equals(update.getZone()));
      // FIXME
    }

    @Override
    public void dumpZone(DumpZoneRequest request) {
      // FIXME
    }

    @Override
    public void knownZones(KnownZonesRequest request) {
      // FIXME
    }
  }
}
