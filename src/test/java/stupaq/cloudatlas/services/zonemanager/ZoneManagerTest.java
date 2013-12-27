package stupaq.cloudatlas.services.zonemanager;

import org.apache.commons.configuration.BaseConfiguration;
import org.apache.commons.configuration.Configuration;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import stupaq.cloudatlas.attribute.Attribute;
import stupaq.cloudatlas.configuration.BootstrapConfiguration;
import stupaq.cloudatlas.configuration.BootstrapConfiguration.Builder;
import stupaq.cloudatlas.configuration.CAConfiguration;
import stupaq.cloudatlas.messaging.messages.AttributesUpdateMessage;
import stupaq.cloudatlas.naming.GlobalName;
import stupaq.cloudatlas.threading.SingleThreadModel;

import static stupaq.cloudatlas.attribute.values.AttributeValueTestUtils.Int;
import static stupaq.cloudatlas.services.zonemanager.hierarchy.ZoneHierarchyTestUtils.Attr;

public class ZoneManagerTest {
  private static final GlobalName THE_ZONE = GlobalName.parse("/test1/test2/test3");
  private static final List<Attribute> SOME_ATTRIBUTES =
      Arrays.<Attribute>asList(Attr("cpu_num", Int(12)), Attr("total_ram", Int(8589934592L)),
          Attr("free_ram", Int(1073741824L)));
  private static final long TIME_SCALE = 250L;
  private BootstrapConfiguration config;
  private ZoneManager manager;

  @Before
  public void setUp() throws Exception {
    config = new Builder().config(prepareConfig()).threadModel(new SingleThreadModel()).create();
    manager = new ZoneManager(config);
    manager.startAsync().awaitRunning();
    // Fill in with some attributes
    config.bus().post(new AttributesUpdateMessage(THE_ZONE, SOME_ATTRIBUTES));
  }

  @After
  public void tearDown() throws Exception {
    manager.stopAsync().awaitTerminated();
  }

  @Test
  public void testSelectContact() throws Exception {
    // FIXME
  }

  @Test
  public void testExportZones() throws Exception {
    // FIXME
  }

  @Test
  public void testUpdateZones() throws Exception {
    // FIXME
  }

  private static CAConfiguration prepareConfig() {
    Configuration config = new BaseConfiguration();
    config.setProperty(ZoneManagerConfigKeys.ZONE_NAME, THE_ZONE.toString());
    config.setProperty(ZoneManagerConfigKeys.REEVALUATION_INTERVAL, TIME_SCALE);
    return new CAConfiguration(config);
  }
}
