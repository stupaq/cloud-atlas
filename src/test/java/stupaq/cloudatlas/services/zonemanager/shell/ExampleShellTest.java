package stupaq.cloudatlas.services.zonemanager.shell;

import com.google.common.base.Preconditions;
import com.google.common.collect.Iterables;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Before;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicInteger;

import stupaq.cloudatlas.attribute.Attribute;
import stupaq.cloudatlas.attribute.AttributeName;
import stupaq.cloudatlas.attribute.AttributeValue;
import stupaq.cloudatlas.attribute.values.CAQuery;
import stupaq.cloudatlas.attribute.values.CATime;
import stupaq.cloudatlas.services.zonemanager.query.InstalledQueriesUpdater;
import stupaq.cloudatlas.query.errors.ParsingException;
import stupaq.cloudatlas.naming.GlobalName;
import stupaq.cloudatlas.query.parser.QueryParser;
import stupaq.cloudatlas.services.zonemanager.ZoneManagementInfo;
import stupaq.cloudatlas.services.zonemanager.hierarchy.ZoneHierarchy;
import stupaq.cloudatlas.services.zonemanager.hierarchy.ZoneHierarchy.InPlaceAggregator;
import stupaq.cloudatlas.services.zonemanager.hierarchy.ZoneHierarchy.InPlaceMapper;
import stupaq.cloudatlas.services.zonemanager.hierarchy.ZoneHierarchyTestUtils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;
import static stupaq.cloudatlas.attribute.values.AttributeValueTestUtils.*;
import static stupaq.cloudatlas.query.typecheck.TypeInfoTestUtils.*;

public class ExampleShellTest {
  private static final Log LOG = LogFactory.getLog(ExampleShellTest.class);
  private static final AtomicInteger uniqueId = new AtomicInteger(0);
  private ZoneHierarchy<ZoneManagementInfo> root;

  private static AttributeName getUniqueQueryName() {
    return AttributeName.valueOfReserved("&unique" + uniqueId.getAndDecrement());
  }

  private static Collection<Entry<AttributeName, CAQuery>> parse(InputStream in)
      throws IOException, IllegalArgumentException, ParsingException {
    HashMap<AttributeName, CAQuery> map = new HashMap<>();
    BufferedReader stdin = new BufferedReader(new InputStreamReader(in));
    String line;
    while ((line = stdin.readLine()) != null && line.length() != 0) {
      line = line.trim();
      if (!line.endsWith(";")) {
        throw new ParsingException("Parse failed");
      }
      line = line.substring(0, line.length() - 1);
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
    ExampleShellTest shell = new ExampleShellTest();
    shell.setUp();
    Collection<Entry<AttributeName, CAQuery>> queries = Collections.emptyList();
    try {
      queries = parse(System.in);
      for (Entry<AttributeName, CAQuery> entry : queries) {
        shell.installQuery(entry.getKey(), entry.getValue());
      }
      shell.recomputeQueries();
      System.out.println("Final hierarchy:\n" + shell.root);
    } catch (Exception e) {
      LOG.error("Failure ", e);
    } finally {
      for (Entry<AttributeName, CAQuery> entry : queries) {
        shell.removeQuery(entry.getKey());
      }
    }
  }

  private AttributeName executeQuery(String query) {
    AttributeName name = getUniqueQueryName();
    installQuery(name, new CAQuery(query));
    recomputeQueries();
    return name;
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

  private void assertValue(String path, String name, AttributeValue value) {
    GlobalName globalName = GlobalName.parse(path);
    assertEquals(value,
        root.find(globalName).get().getPayload().getAttribute(AttributeName.valueOf(name)).get()
            .getValue());
  }

  private void assertNothing(String path, String name) {
    GlobalName globalName = GlobalName.parse(path);
    assertFalse(root.find(globalName).get().getPayload().getAttribute(AttributeName.valueOf(name))
        .isPresent());
  }

  @Before
  public void setUp() {
    root = ZoneHierarchyTestUtils.officialExampleHierarchy();
  }

  @Test(expected = ParsingException.class)
  public void testBad0() throws Exception {
    executeQuery("SELECT 2 + 2 AS SELECT");
    fail();
  }

  @Test
  public void testBad1() throws Exception {
    executeQuery("SELECT 2 + 2 AS smth, 3 + 3 AS smth");
    // Queries are atomic, nothing should happen really
    assertNothing("/", "smth");
    assertNothing("/uw", "smth");
    assertNothing("/pjwstk", "smth");
  }

  @Test(expected = ParsingException.class)
  public void testBad2() throws Exception {
    executeQuery("SELECT \"haskell\" AS x'");
    fail();
  }

  @Test
  public void testBad3() throws Exception {
    executeQuery("SELECT \"a\" + 2 AS a2");
    assertNothing("/", "a2");
    assertNothing("/uw", "a2");
    assertNothing("/pjwstk", "a2");
  }

  @Test
  public void testBad4() throws Exception {
    executeQuery("SELECT unfold(contacts) AS a");
    assertNothing("/", "a");
    assertNothing("/uw", "a");
    assertNothing("/pjwstk", "a");
  }

  @Test
  public void testBad5() throws Exception {
    executeQuery("SELECT level AS b");
    assertNothing("/", "b");
    assertNothing("/uw", "b");
    assertNothing("/pjwstk", "b");
  }

  @Test
  public void testBad6() throws Exception {
    // This is a type error, even though some interpreters accept this query
    executeQuery("SELECT (SELECT avg(cpu_usage) WHERE isnull(cpu_usage)) + \"string\" AS smth");
    assertNothing("/uw", "smth");
  }

  @Test
  public void testBad7() throws Exception {
    executeQuery("SELECT land(lor(some_names+\"xx\" REGEXP \"*atkax*\")) AS has_beatka");
    assertNothing("/", "has_beatka");
  }

  @Test
  public void testBad8() throws Exception {
    executeQuery("SELECT lor(land(some_names)) AS beatka");
    assertNothing("/", "beatka");
  }

  @Test
  public void testBad9() throws Exception {
    executeQuery("SELECT (SELECT avg(cpu_usage) WHERE false) AS smth1");
    assertValue("/uw", "smth1", Doub());
    // This is a type error, even though some interpreters accept this query
    executeQuery("SELECT (SELECT avg(cpu_usage) WHERE false) + \"string\" AS smth");
    assertNothing("/uw", "smth");
  }

  @Test
  public void testBad10() throws Exception {
    executeQuery("SELECT min(unfold(some_names) + \"xx\") AS smt");
    assertValue("/uw", "smt", Str("agatkaxx"));
    executeQuery("SELECT min(unfold(some_names + \"xx\")) AS beatka");
    assertNothing("/uw", "beatka");
  }

  @Test
  public void testBad11() throws Exception {
    executeQuery("SELECT (SELECT 2, 3) AS smth");
    assertNothing("/", "smth");
  }

  @Test
  public void testBad12() throws Exception {
    executeQuery("SELECT 2 AS two, 2 AS two");
    executeQuery("SELECT (SELECT 3 AS three) AS four");
    assertNothing("/", "two");
    assertNothing("/", "three");
    assertNothing("/", "four");
  }

  @Test
  public void testBad13() throws Exception {
    executeQuery("SELECT 2 AS &special, true AS control");
    assertNothing("/", "control");
  }

  @Test
  public void testBad14() throws Exception {
    executeQuery("SELECT epoch() + epoch() AS plus");
    executeQuery("SELECT epoch() - epoch() AS minus");
    assertNothing("/", "plus");
    assertValue("/", "minus", Dur(0));
  }

  @Test
  public void testBad15() throws Exception {
    executeQuery("SELECT first(\"baba\", name) AS names");
    executeQuery("SELECT first(1) AS missing");
    executeQuery("SELECT first(1, name, 3) AS extra");
    executeQuery("SELECT first(1, name) AS ok ORDER BY name ASC NULLS LAST");
    assertNothing("/", "names");
    assertNothing("/", "missing");
    assertNothing("/", "extra");
    assertValue("/", "ok", List(TStr(), Str("pjwstk")));
  }

  @Test
  public void testBad16() throws Exception {
    executeQuery("SELECT first(1, name) AS test1 ORDER BY contacts");
    executeQuery("SELECT min(contacts) AS test2");
    executeQuery("SELECT to_set(first(100, distinct(unfold(contacts)))) AS test3");
    executeQuery("SELECT min(contacts) AS test4 WHERE false");
    assertNothing("/", "test1");
    assertNothing("/", "test2");
    assertValue("/uw", "test3",
        Set(TCont(), Cont("UW1A"), Cont("UW1B"), Cont("UW1C"), Cont("UW2A"), Cont("UW3A"),
            Cont("UW3B")));
    assertNothing("/uw", "test4");
  }

  @Test
  public void testBad17() throws Exception {
    executeQuery("SELECT avg(name) AS test1 WHERE false");
    assertNothing("/uw", "test1");
  }

  @Test
  public void testBad18() throws Exception {
    executeQuery("SELECT sum(has_ups) AS test1 WHERE is_null(has_ups)");
    assertNothing("/uw", "test1");
  }

  @Test
  public void testBad19() throws Exception {
    executeQuery("SELECT avg(timestamp) AS test1");
    executeQuery("SELECT avg(timestamp) AS test2 WHERE false");
    assertNothing("/", "test1");
    assertNothing("/", "test2");
  }

  @Test
  public void testBad20() throws Exception {
    executeQuery("SELECT first(1, contacts + cardinality) AS test1 WHERE false");
    executeQuery("SELECT count(cardinality) AS test2 WHERE false");
    assertNothing("/", "test1");
    assertValue("/", "test2", Int(0));
  }

  @Test
  public void testBad21() throws Exception {
    executeQuery("SELECT first(1, unfold(name)) AS test1 WHERE false");
    assertNothing("/", "test1");
  }

  @Test
  public void testExample0() throws Exception {
    executeQuery("SELECT 2 + 2 AS two_plus_two");
    assertValue("/", "two_plus_two", Int(4));
  }

  @Test
  public void testExample1() throws Exception {
    executeQuery("SELECT count(members) AS members_count");
    assertValue("/uw", "members_count", Int(3));
    assertNothing("/", "members_count");
  }

  @Test
  public void testExample2() throws Exception {
    executeQuery(
        "SELECT first(2, unfold(contacts)) AS new_contacts ORDER BY num_cores ASC NULLS FIRST, "
        + "cpu_usage DESC NULLS LAST");
    assertValue("/uw", "new_contacts", List(TCont(), Cont("UW1A"), Cont("UW1B")));
    assertNothing("/", "new_contacts");
  }

  @Test
  public void testExample2v1() throws Exception {
    executeQuery("SELECT first(2, unfold(contacts)) AS new_contacts ORDER BY cpu_usage "
                 + "DESC NULLS LAST, num_cores ASC NULLS FIRST");
    assertValue("/uw", "new_contacts", List(TCont(), Cont("UW3A"), Cont("UW3B")));
  }

  @Test
  public void testExample3() throws Exception {
    executeQuery("SELECT sum(num_cores * 2) AS ncores WHERE cpu_usage < ( SELECT avg(cpu_usage))");
    assertValue("/pjwstk", "ncores", Int(14));
    assertNothing("/", "ncores");
  }

  @Test
  public void testExample4() throws Exception {
    executeQuery("SELECT count(num_cores - size(some_names)) AS sth");
    assertValue("/uw", "sth", Int(2));
    assertNothing("/pjwstk", "sth");
  }

  @Test
  public void testExample5() throws Exception {
    executeQuery(
        "SELECT min(sum(distinct(2*level)) + 38 * size(contacts)) AS sth WHERE num_cores < 8");
    assertValue("/uw", "sth", Int(42));
    assertNothing("/", "sth");
  }

  @Test
  public void testExample6() throws Exception {
    executeQuery("SELECT random(10, cpu_usage * num_cores / 10) AS sth");
    assertValue("/pjwstk", "sth", List(TDoub(), Doub(0.07), Doub(0.52)));
    assertNothing("/", "sth");
  }

  @Test
  public void testExample7v2() throws Exception {
    executeQuery("SELECT first(1, name) AS concat_name WHERE num_cores >= "
                 + "(SELECT min(num_cores) ORDER BY timestamp) ORDER BY creation ASC NULLS "
                 + "LAST");
    assertValue("/pjwstk", "concat_name", List(TStr(), Str("whatever01")));
  }

  @Test
  public void testExample8() throws Exception {
    executeQuery("SELECT sum(cardinality) AS cardinality");
    assertValue("/", "cardinality", Int(5));
    assertValue("/uw", "cardinality", Int(3));
    assertValue("/pjwstk", "cardinality", Int(2));
  }

  @Test
  public void testExample9() throws Exception {
    executeQuery("SELECT to_set(random(100, unfold(contacts))) AS contacts");
    assertValue("/", "contacts",
        Set(TCont(), Cont("PJ1"), Cont("PJ2"), Cont("UW1A"), Cont("UW1B"), Cont("UW1C"),
            Cont("UW2A"), Cont("UW3A"), Cont("UW3B")));
  }

  @Test
  public void testExample10() throws Exception {
    executeQuery("SELECT land(cpu_usage < 0.5) AS cpu_ok");
    assertValue("/pjwstk", "cpu_ok", Bool(true));
    assertValue("/uw", "cpu_ok", Bool(false));
  }

  @Test
  public void testExample11() throws Exception {
    executeQuery(
        "SELECT min(name) AS min_name, to_string(first(1, name)) AS max_name ORDER BY name DESC");
    assertValue("/", "min_name", Str("pjwstk"));
    assertValue("/", "max_name", Str("[ uw ]"));
  }

  @Test
  public void testExample12() throws Exception {
    executeQuery("SELECT epoch() AS epoch, land(timestamp > epoch()) AS afterY2K");
    assertValue("/", "afterY2K", Bool(true));
    assertValue("/pjwstk", "afterY2K", Bool(true));
    assertValue("/uw", "afterY2K", Bool(true));
    assertValue("/", "epoch", Str("2000/01/01 00:00:00.000 CET").to().Time());
    assertValue("/", "epoch", CATime.epoch());
    assertValue("/pjwstk", "epoch", CATime.epoch());
    assertValue("/uw", "epoch", CATime.epoch());
  }

  @Test
  public void testExample13() throws Exception {
    executeQuery("SELECT min(timestamp) + (max(timestamp) - epoch())/2 AS t2");
    // The problem here is that (at least for Java) CET is not defined for dates within DST.
    // I have decided to follow the convention and dates from DST must be specified with CEST
    // time zone. Internal representation is global in the sense that it uses an offset from Unix
    // "epoch time", so this assumption affects string representation only.
    assertValue("/uw", "t2", Str("2019/04/16 05:31:30.000 CEST").to().Time());
  }

  @Test
  public void testExample14() throws Exception {
    executeQuery("SELECT avg(cpu_usage) AS anull WHERE isnull(cpu_usage)");
    assertValue("/uw", "anull", Doub());
  }

  @Test
  public void testExample15() throws Exception {
    executeQuery("SELECT (SELECT avg(cpu_usage) WHERE false) AS smth");
    assertValue("/uw", "smth", Doub());
  }

  @Test
  public void testExample16() throws Exception {
    installQuery(AttributeName.valueOfReserved("&ex16_1"),
        new CAQuery("SELECT first(1, some_names) AS some_names ORDER BY name ASC"));
    installQuery(AttributeName.valueOfReserved("&ex16_2"),
        new CAQuery("SELECT count(distinct(unfold(some_names))) AS names_no ORDER BY name"));
    recomputeQueries();
    assertValue("/uw", "some_names", List(TList(TStr()), List(TStr())));
    assertValue("/", "some_names", List(TList(TList(TStr())), List(TList(TStr()), List(TStr()))));
    assertValue("/uw", "names_no", Int(5));
    assertValue("/", "names_no", Int(1));
  }

  @Test
  public void testExample17() throws Exception {
    removeQuery(executeQuery("SELECT sum(cardinality) AS base"));
    executeQuery("SELECT sum(cardinality) + sum(base) AS base");
    assertValue("/", "base", Int(5));
    assertValue("/uw", "base", Int(3));
    assertValue("/pjwstk", "base", Int(2));
  }

  @Test
  public void testExample18() throws Exception {
    executeQuery("SELECT min(cpu_usage) AS min_usage, max(cpu_usage) AS max_usage");
    assertValue("/uw", "min_usage", Doub(0.1));
    assertValue("/uw", "max_usage", Doub(0.9));
  }

  @Test
  public void testExample19() throws Exception {
    executeQuery("SELECT to_set(first(100, name)) AS ups_names WHERE has_ups");
    assertValue("/uw", "ups_names", Set(TStr(), Str("khaki13")));
  }

  @Test
  public void testExample20() throws Exception {
    executeQuery("SELECT first(100, is_null(has_ups)) AS ups_nulls ORDER BY name ASC");
    assertValue("/uw", "ups_nulls", List(TBool(), Bool(false), Bool(false), Bool(true)));
  }

  @Test
  public void testExample21() throws Exception {
    executeQuery("SELECT (2 < 3) AND (3 > 2) AND NOT (2 > 3) AND NOT (3 < 2) AND (2 <> 3) AND "
                 + "(2 = 2) AND (2 <= 2) AND (2 <= 3) AND (3 >= 2) AND (2 >= 2) AS test");
    assertValue("/", "test", Bool(true));
  }

  @Test
  public void testExample22() throws Exception {
    executeQuery("SELECT to_set(first(1, members)) AS members ORDER BY name");
    assertValue("/", "members",
        Set(TSet(TSet(TCont())), Set(TSet(TCont()), Set(TCont(), Cont("PJ1")))));
  }

  @Test
  public void testExample23() throws Exception {
    executeQuery("SELECT avg(epoch() - epoch()) AS test1");
    assertNothing("/", "test1");
  }

  /*
  @Test
  public void testExample() throws Exception {
    executeQuery("");
    assertValue("/", "", );
  }
  */

  /*
  @Test
  public void testBad() throws Exception {
    executeQuery("");
    assertNothing("/", "");
  }
  */

  @Test
  public void testParse() throws IOException, IllegalArgumentException, ParsingException {
    Collection<Entry<AttributeName, CAQuery>> queries = parse(new ByteArrayInputStream(
        "&example1: SELECT 2 + 2 AS four;\n&example2: SELECT 2 + 3 AS five;".getBytes()));
    Map<AttributeName, CAQuery> expected = new HashMap<>();
    expected.put(AttributeName.valueOfReserved("&example1"), new CAQuery("SELECT 2 + 2 AS four"));
    expected.put(AttributeName.valueOfReserved("&example2"), new CAQuery("SELECT 2 + 3 AS five"));
    assertEquals(expected.entrySet(), queries);
  }
}
