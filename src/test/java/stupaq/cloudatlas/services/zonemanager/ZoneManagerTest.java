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
import stupaq.cloudatlas.naming.EntityName;
import stupaq.cloudatlas.naming.GlobalName;
import stupaq.cloudatlas.services.rmiserver.handler.LocalClientHandler;
import stupaq.cloudatlas.services.rmiserver.protocol.LocalClientProtocol;
import stupaq.cloudatlas.threading.SingleThreadModel;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static stupaq.cloudatlas.attribute.values.AttributeValueTestUtils.Int;
import static stupaq.cloudatlas.services.zonemanager.hierarchy.ZoneHierarchyTestUtils.Attr;
import static stupaq.cloudatlas.services.zonemanager.hierarchy.ZoneHierarchyTestUtils.Name;

public class ZoneManagerTest {
  private static final GlobalName THE_ZONE = GlobalName.parse("/test1/test2/test3");
  private static final List<Attribute> SOME_ATTRIBUTES =
      Arrays.<Attribute>asList(Attr("cpu_num", Int(12)), Attr("total_ram", Int(8589934592L)),
          Attr("free_ram", Int(1073741824L)));
  private static final long TIME_SCALE = 250L;
  private BootstrapConfiguration config;
  private ZoneManager manager;
  private LocalClientProtocol client;

  @Before
  public void setUp() throws Exception {
    config = new Builder().config(prepareConfig()).threadModel(new SingleThreadModel()).create();
    manager = new ZoneManager(config);
    manager.startAsync().awaitRunning();
    client = new LocalClientHandler(config.bus());
    // Fill in with some attributes
    client.updateAttributes(THE_ZONE, SOME_ATTRIBUTES);
  }

  @After
  public void tearDown() throws Exception {
    manager.stopAsync().awaitTerminated();
  }

  @Test
  public void testUpdateAttributes() throws Exception {
    List<Attribute> attributes = client.getValues(asList(new EntityName(THE_ZONE, Name("free_ram")),
        new EntityName(THE_ZONE, Name("nothing_that_exists"))));
    assertNotNull(attributes.get(0));
    assertEquals(Name("free_ram"), attributes.get(0).name());
    assertEquals(Int(1073741824L), attributes.get(0).value());
    assertNull(attributes.get(1));
  }

  private static CAConfiguration prepareConfig() {
    Configuration config = new BaseConfiguration();
    config.setProperty(ZoneManagerConfigKeys.ZONE_NAME, THE_ZONE.toString());
    config.setProperty(ZoneManagerConfigKeys.REEVALUATION_INTERVAL, TIME_SCALE);
    return new CAConfiguration(config);
  }
}
