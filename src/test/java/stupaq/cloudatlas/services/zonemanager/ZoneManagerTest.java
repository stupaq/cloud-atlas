package stupaq.cloudatlas.services.zonemanager;

import com.google.common.eventbus.Subscribe;

import org.apache.commons.configuration.BaseConfiguration;
import org.apache.commons.configuration.Configuration;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import stupaq.cloudatlas.messaging.MessageBus;
import stupaq.cloudatlas.messaging.MessageListener;
import stupaq.cloudatlas.messaging.messages.KnownZonesResponse;
import stupaq.cloudatlas.naming.GlobalName;
import stupaq.commons.util.concurrent.AsynchronousInvoker.DirectInvocation;

public class ZoneManagerTest {
  private static final GlobalName GLOBAL_NAME = GlobalName.parse("/test1/test2/test3");
  private Configuration configuration;
  private MessageBus bus;
  private ZoneManager manager;

  @Before
  public void setUp() throws Exception {
    configuration = new BaseConfiguration();
    bus = new MessageBus();
    manager = new ZoneManager(configuration, bus, GLOBAL_NAME);
    manager.startAsync().awaitRunning();
  }

  @After
  public void tearDown() throws Exception {
    manager.stopAsync().awaitTerminated();
  }

  @Test
  public void testKnownZones() throws Exception {
  }

  private static interface TestListener extends MessageListener {
    @Subscribe
    @DirectInvocation
    public void knownZones(KnownZonesResponse response);
  }
}
