package stupaq.cloudatlas.interpreter.shell;

import com.google.common.base.Preconditions;
import com.google.common.collect.Iterables;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Before;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map.Entry;

import stupaq.cloudatlas.attribute.Attribute;
import stupaq.cloudatlas.attribute.AttributeName;
import stupaq.cloudatlas.attribute.AttributeValue;
import stupaq.cloudatlas.attribute.types.CAQuery;
import stupaq.cloudatlas.interpreter.InstalledQueriesUpdater;
import stupaq.cloudatlas.naming.GlobalName;
import stupaq.cloudatlas.parser.QueryParser;
import stupaq.cloudatlas.zone.ZoneManagementInfo;
import stupaq.cloudatlas.zone.hierarchy.ZoneHierarchy;
import stupaq.cloudatlas.zone.hierarchy.ZoneHierarchy.InPlaceAggregator;
import stupaq.cloudatlas.zone.hierarchy.ZoneHierarchy.InPlaceMapper;
import stupaq.cloudatlas.zone.hierarchy.ZoneHierarchyTestUtils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;
import static stupaq.cloudatlas.attribute.types.AttributeTypeTestUtils.*;

public class ExampleShell {
  private static final Log LOG = LogFactory.getLog(ExampleShell.class);
  private ZoneHierarchy<ZoneManagementInfo> root;

  private static Collection<Entry<AttributeName, CAQuery>> parse() throws Exception {
    HashMap<AttributeName, CAQuery> map = new HashMap<>();
    BufferedReader stdin = new BufferedReader(new InputStreamReader(System.in));
    String line;
    while ((line = stdin.readLine()) != null && line.length() != 0) {
      String[] parts = line.split(":");
      if (parts.length < 2) {
        throw new Exception("Parse failed");
      }
      AttributeName name = AttributeName.valueOfReserved(parts[0].trim());
      CAQuery query = new CAQuery(parts[1].trim());
      if (map.containsKey(name)) {
        throw new Exception("Duplicated query name");
      }
      map.put(name, query);
    }
    return map.entrySet();
  }

  /** MAIN */
  public static void main(String[] args) {
    try {
      ExampleShell shell = new ExampleShell();
      shell.setUp();
      Collection<Entry<AttributeName, CAQuery>> queries = parse();
      try {
        for (Entry<AttributeName, CAQuery> entry : queries) {
          shell.installQuery(entry.getKey(), entry.getValue());
        }
        shell.recomputeQueries();
        System.out.println(shell.root);
      } finally {
        for (Entry<AttributeName, CAQuery> entry : queries) {
          shell.removeQuery(entry.getKey());
        }
      }
    } catch (Exception e) {
      LOG.error("Failure ", e);
    }
  }

  private void executeQuery(String name, String query) {
    installQuery(AttributeName.valueOfReserved(name), new CAQuery(query));
    recomputeQueries();
  }

  private void installQuery(final AttributeName name, final CAQuery query) {
    Preconditions.checkArgument(name.isSpecial());
    try {
      new QueryParser(query.getQueryString()).parseProgram();
    } catch (Exception e) {
      throw new IllegalArgumentException(
          "Provided query was rejected by parser. Will not be installed.");
    }
    root.zipFromLeaves(new InPlaceAggregator<ZoneManagementInfo>() {
      @Override
      protected void process(Iterable<ZoneManagementInfo> children,
          ZoneManagementInfo managementInfo) {
        if (!Iterables.isEmpty(children)) {
          managementInfo.updateAttribute(new Attribute<>(name, query));
        }
      }
    });
  }

  private void removeQuery(final AttributeName name) {
    Preconditions.checkArgument(name.isSpecial());
    root.mapAll(new InPlaceMapper<ZoneManagementInfo>() {
      @Override
      protected void process(ZoneManagementInfo managementInfo) {
        managementInfo.removeAttribute(name);
      }
    });
  }

  private void recomputeQueries() {
    root.zipFromLeaves(new InstalledQueriesUpdater());
  }

  private void assertSet(String path, String name, AttributeValue value) {
    GlobalName globalName = GlobalName.parse(path);
    assertEquals(value,
        root.find(globalName).get().getPayload().getAttribute(AttributeName.valueOf(name)).get()
            .getValue().get());
  }

  private void assertNotSet(String path, String name) {
    GlobalName globalName = GlobalName.parse(path);
    assertFalse(root.find(globalName).get().getPayload().getAttribute(AttributeName.valueOf(name))
        .isPresent());
  }

  private void dumpHierarchy() {
    LOG.info("Final hierarchy\n" + root);
  }

  @Before
  public void setUp() {
    root = ZoneHierarchyTestUtils.officialExampleHierarchy();
  }

  @Test(expected = IllegalArgumentException.class)
  public void testBad0() throws Exception {
    executeQuery("&bad0", "SELECT 2 + 2 AS SELECT");
    fail();
  }

  @Test
  public void testBad1() throws Exception {
    executeQuery("&bad1", "SELECT 2 + 2 AS smth, 3 + 3 AS smth");
    // Queries are atomic, nothing should happen really
    assertNotSet("/", "smth");
    assertNotSet("/uw", "smth");
    assertNotSet("/pjwstk", "smth");
  }

  @Test
  public void testExample0() throws Exception {
    executeQuery("&two_plus_two", "SELECT 2 + 2 AS two_plus_two");
    assertSet("/", "two_plus_two", Int(4));
  }

  @Test
  public void testExample1() throws Exception {
    executeQuery("&ex1", "SELECT count(members) AS members_count");
    assertSet("/uw", "members_count", Int(3));
    assertNotSet("/", "members_count");
  }

  @Test
  public void testExample2() throws Exception {
    executeQuery("&ex2",
        "SELECT first(2, unfold(contacts)) AS new_contacts ORDER BY num_cores ASC NULLS FIRST, "
        + "cpu_usage DESC NULLS LAST");
    assertSet("/uw", "new_contacts", List(Cont("UW1B"), Cont("UW1A")));
    assertNotSet("/", "new_contacts");
  }

  @Test
  public void testExample2v1() throws Exception {
    executeQuery("&ex2", "SELECT first(2, unfold(contacts)) AS new_contacts ORDER BY cpu_usage "
                         + "DESC NULLS LAST, num_cores ASC NULLS FIRST");
    assertSet("/uw", "new_contacts", List(Cont("UW3A"), Cont("UW3B")));
  }

  @Test
  public void testExample3() throws Exception {
    executeQuery("&ex3",
        "SELECT sum(num_cores * 2) AS ncores WHERE cpu_usage < ( SELECT avg(cpu_usage))");
    assertSet("/pjwstk", "ncores", Int(14));
    assertNotSet("/", "ncores");
  }

  @Test
  public void testExample4() throws Exception {
    executeQuery("&ex4", "SELECT count(num_cores - size(some_names)) AS sth");
    assertSet("/uw", "sth", Int(2));
    assertNotSet("/pjwstk", "sth");
  }

  @Test
  public void testExample5() throws Exception {
    executeQuery("&ex5",
        "SELECT min(sum(distinct(2*level)) + 38 * size(contacts)) AS sth WHERE num_cores < 8");
    assertSet("/uw", "sth", Int(42));
    assertNotSet("/", "sth");
  }

  @Test
  public void testExample6() throws Exception {
    executeQuery("&ex6", "SELECT random(10, cpu_usage * num_cores / 10) AS sth");
    assertSet("/pjwstk", "sth", List(Doub(0.07), Doub(0.52)));
    assertNotSet("/", "sth");
  }

  @Test
  public void testExample7v2() throws Exception {
    executeQuery("&ex7", "SELECT first(1, name) AS concat_name WHERE num_cores >= "
                         + "(SELECT min(num_cores) ORDER BY timestamp) ORDER BY creation ASC NULLS "
                         + "LAST");
    assertSet("/pjwstk", "concat_name", List(Str("whatever01")));
  }

  /*
  @Test
  public void testExample() throws Exception {
    executeQuery("&ex", "");
    assertSet("/", "", );
  }
  */
}
