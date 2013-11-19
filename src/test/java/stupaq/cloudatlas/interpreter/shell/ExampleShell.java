package stupaq.cloudatlas.interpreter.shell;

import com.google.common.base.Preconditions;
import com.google.common.collect.Iterables;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Before;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map.Entry;

import stupaq.cloudatlas.attribute.Attribute;
import stupaq.cloudatlas.attribute.AttributeName;
import stupaq.cloudatlas.attribute.AttributeValue;
import stupaq.cloudatlas.attribute.types.CAQuery;
import stupaq.cloudatlas.attribute.types.CATime;
import stupaq.cloudatlas.interpreter.InstalledQueriesUpdater;
import stupaq.cloudatlas.interpreter.errors.ParsingException;
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

  private static Collection<Entry<AttributeName, CAQuery>> parse()
      throws IOException, IllegalArgumentException, ParsingException {
    HashMap<AttributeName, CAQuery> map = new HashMap<>();
    BufferedReader stdin = new BufferedReader(new InputStreamReader(System.in));
    String line;
    while ((line = stdin.readLine()) != null && line.length() != 0) {
      String[] parts = line.split(":");
      if (parts.length < 2) {
        throw new ParsingException("Parse failed");
      }
      AttributeName name = AttributeName.valueOfReserved(parts[0].trim());
      CAQuery query = new CAQuery(parts[1].trim());
      if (map.containsKey(name)) {
        throw new IllegalArgumentException("Duplicated query name");
      }
      map.put(name, query);
    }
    return map.entrySet();
  }

  /** MAIN */
  public static void main(String[] args) {
    ExampleShell shell = new ExampleShell();
    shell.setUp();
    Collection<Entry<AttributeName, CAQuery>> queries = Collections.emptyList();
    try {
      queries = parse();
      for (Entry<AttributeName, CAQuery> entry : queries) {
        shell.installQuery(entry.getKey(), entry.getValue());
      }
      shell.recomputeQueries();
      System.out.println(shell.root);
    } catch (Exception e) {
      LOG.error("Failure ", e);
    } finally {
      for (Entry<AttributeName, CAQuery> entry : queries) {
        shell.removeQuery(entry.getKey());
      }
    }
  }

  private void executeQuery(String name, String query) {
    installQuery(AttributeName.valueOfReserved(name), new CAQuery(query));
    recomputeQueries();
  }

  private void installQuery(final AttributeName name, final CAQuery query) {
    Preconditions.checkArgument(name.isSpecial());
    try (QueryParser parser = new QueryParser(query.getQueryString())) {
      parser.parseProgram();
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
            .get().get());
  }

  private void assertSetNull(String path, String name) {
    GlobalName globalName = GlobalName.parse(path);
    assertFalse(
        root.find(globalName).get().getPayload().getAttribute(AttributeName.valueOf(name)).get()
            .get().isPresent());
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

  @Test(expected = ParsingException.class)
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

  @Test(expected = ParsingException.class)
  public void testBad2() throws Exception {
    executeQuery("&bad2", "SELECT \"haskell\" AS x'");
    fail();
  }

  @Test
  public void testBad3() throws Exception {
    executeQuery("&bad3", "SELECT \"a\" + 2 AS a2");
    assertNotSet("/", "a2");
    assertNotSet("/uw", "a2");
    assertNotSet("/pjwstk", "a2");
  }

  @Test
  public void testBad4() throws Exception {
    executeQuery("&bad4", "SELECT unfold(contacts) AS a");
    assertNotSet("/", "a");
    assertNotSet("/uw", "a");
    assertNotSet("/pjwstk", "a");
  }

  @Test
  public void testBad5() throws Exception {
    executeQuery("&bad5", "SELECT level AS b");
    assertNotSet("/", "b");
    assertNotSet("/uw", "b");
    assertNotSet("/pjwstk", "b");
  }

  @Test
  public void testBad6() throws Exception {
    // This is a type error, even though some interpreters accept this query
    executeQuery("&bad7",
        "SELECT (SELECT avg(cpu_usage) WHERE isnull(cpu_usage)) + \"string\" AS smth");
    assertNotSet("/uw", "smth");
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

  @Test
  public void testExample8() throws Exception {
    executeQuery("&ex8", "SELECT sum(cardinality) AS cardinality");
    assertSet("/", "cardinality", Int(5));
    assertSet("/uw", "cardinality", Int(3));
    assertSet("/pjwstk", "cardinality", Int(2));
  }

  @Test
  public void testExample9() throws Exception {
    executeQuery("&ex9", "SELECT to_set(random(100, unfold(contacts))) AS contacts");
    assertSet("/", "contacts",
        Set(Cont("PJ1"), Cont("PJ2"), Cont("UW1A"), Cont("UW1B"), Cont("UW1C"), Cont("UW2A"),
            Cont("UW3A"), Cont("UW3B")));
  }

  @Test
  public void testExample10() throws Exception {
    executeQuery("&ex10", "SELECT land(cpu_usage < 0.5) AS cpu_ok");
    assertSet("/pjwstk", "cpu_ok", Bool(true));
    assertSet("/uw", "cpu_ok", Bool(false));
  }

  @Test
  public void testExample11() throws Exception {
    executeQuery("&ex11",
        "SELECT min(name) AS min_name, to_string(first(1, name)) AS max_name ORDER BY name DESC");
    assertSet("/", "min_name", Str("pjwstk"));
    assertSet("/", "max_name", Str("[ uw ]"));
  }

  @Test
  public void testExample12() throws Exception {
    executeQuery("&ex12", "SELECT epoch() AS epoch, land(timestamp > epoch()) AS afterY2K");
    assertSet("/", "afterY2K", Bool(true));
    assertSet("/pjwstk", "afterY2K", Bool(true));
    assertSet("/uw", "afterY2K", Bool(true));
    assertSet("/", "epoch", Str("2000/01/01 00:00:00.000 CET").to().Time());
    assertSet("/", "epoch", CATime.epoch());
    assertSet("/pjwstk", "epoch", CATime.epoch());
    assertSet("/uw", "epoch", CATime.epoch());
  }

  @Test
  public void testExample13() throws Exception {
    executeQuery("&ex13", "SELECT min(timestamp) + (max(timestamp) - epoch())/2 AS t2");
    // The problem here is that (at least for Java) CET is not defined for dates within DST.
    // I have decided to follow the convention and dates from DST must be specified with CEST
    // time zone. Internal representation is global in the sense that it uses an offset from Unix
    // "epoch time", so this assumption affects string representation only.
    assertSet("/uw", "t2", Str("2019/04/16 05:31:30.000 CEST").to().Time());
  }

  @Test
  public void testExample14() throws Exception {
    executeQuery("&bad14", "SELECT avg(cpu_usage) AS anull WHERE isnull(cpu_usage)");
    assertSetNull("/uw", "anull");
  }

  /*
  @Test
  public void testExample() throws Exception {
    executeQuery("&ex", "");
    assertSet("/", "", );
  }
  */

  /*
  @Test
  public void testBad() throws Exception {
    executeQuery("&bad", "");
    assertNotSet("/", "");
  }
  */
}
