package stupaq.cloudatlas.services.zonemanager;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import stupaq.cloudatlas.configuration.BootstrapConfiguration;
import stupaq.cloudatlas.configuration.BootstrapConfiguration.Builder;
import stupaq.cloudatlas.messaging.MessageBus;
import stupaq.cloudatlas.naming.GlobalName;
import stupaq.cloudatlas.threading.SingleThreadModel;

public class ZoneManagerTest {
  private static final GlobalName GLOBAL_NAME = GlobalName.parse("/test1/test2/test3");
  private BootstrapConfiguration configuration;
  private MessageBus bus;
  private ZoneManager manager;

  @Before
  public void setUp() throws Exception {
    configuration =
        new Builder().leafZone(GLOBAL_NAME).threadModel(new SingleThreadModel()).create();
    manager = new ZoneManager(configuration);
    manager.startAsync().awaitRunning();
  }

  @After
  public void tearDown() throws Exception {
    manager.stopAsync().awaitTerminated();
  }

  @Test
  public void testKnownZones() throws Exception {
    // FIXME
  }
}
