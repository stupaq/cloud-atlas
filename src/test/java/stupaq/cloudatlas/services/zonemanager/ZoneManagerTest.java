package stupaq.cloudatlas.services.zonemanager;

import org.apache.commons.configuration.BaseConfiguration;
import org.apache.commons.configuration.Configuration;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import stupaq.cloudatlas.configuration.BootstrapConfiguration;
import stupaq.cloudatlas.configuration.BootstrapConfiguration.Builder;
import stupaq.cloudatlas.configuration.CAConfiguration;
import stupaq.cloudatlas.messaging.MessageBus;
import stupaq.cloudatlas.naming.GlobalName;
import stupaq.cloudatlas.threading.SingleThreadModel;

public class ZoneManagerTest {
  private static final GlobalName GLOBAL_NAME = GlobalName.parse("/test1/test2/test3");
  private BootstrapConfiguration config;
  private MessageBus bus;
  private ZoneManager manager;

  @Before
  public void setUp() throws Exception {
    config =
        new Builder().config(prepareConfig()).threadModel(new SingleThreadModel()).create();
    manager = new ZoneManager(config);
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

  private static CAConfiguration prepareConfig() {
    Configuration config = new BaseConfiguration();
    config.setProperty(ZoneManagerConfigKeys.ZONE_NAME, GLOBAL_NAME.toString());
    return new CAConfiguration(config);
  }
}
