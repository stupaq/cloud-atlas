package stupaq.cloudatlas.interpreter.shell;

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
import static stupaq.cloudatlas.attribute.types.AttributeValueTestUtils.*;
import static stupaq.cloudatlas.interpreter.typecheck.TypeInfoTestUtils.*;

public class ExampleShell {
  private static final Log LOG = LogFactory.getLog(ExampleShell.class);
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
    ExampleShell shell = new ExampleShell();
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

  private void assertSet(String path, String name, AttributeValue value) {
    GlobalName globalName = GlobalName.parse(path);
    assertEquals(value,
        root.find(globalName).get().getPayload().getAttribute(AttributeName.valueOf(name)).get()
            .getValue());
  }

  private void assertNotSet(String path, String name) {
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
    assertNotSet("/", "smth");
    assertNotSet("/uw", "smth");
    assertNotSet("/pjwstk", "smth");
  }

  @Test(expected = ParsingException.class)
  public void testBad2() throws Exception {
    executeQuery("SELECT \"haskell\" AS x'");
    fail();
  }

  @Test
  public void testBad3() throws Exception {
    executeQuery("SELECT \"a\" + 2 AS a2");
    assertNotSet("/", "a2");
    assertNotSet("/uw", "a2");
    assertNotSet("/pjwstk", "a2");
  }

  @Test
  public void testBad4() throws Exception {
    executeQuery("SELECT unfold(contacts) AS a");
    assertNotSet("/", "a");
    assertNotSet("/uw", "a");
    assertNotSet("/pjwstk", "a");
  }

  @Test
  public void testBad5() throws Exception {
    executeQuery("SELECT level AS b");
    assertNotSet("/", "b");
    assertNotSet("/uw", "b");
    assertNotSet("/pjwstk", "b");
  }

  @Test
  public void testBad6() throws Exception {
    // This is a type error, even though some interpreters accept this query
    executeQuery("SELECT (SELECT avg(cpu_usage) WHERE isnull(cpu_usage)) + \"string\" AS smth");
    assertNotSet("/uw", "smth");
  }

  @Test
  public void testBad7() throws Exception {
    executeQuery("SELECT land(lor(some_names+\"xx\" REGEXP \"*atkax*\")) AS has_beatka");
    assertNotSet("/", "has_beatka");
  }

  @Test
  public void testBad8() throws Exception {
    executeQuery("SELECT lor(land(some_names)) AS beatka");
    assertNotSet("/", "beatka");
  }

  @Test
  public void testBad9() throws Exception {
    executeQuery("SELECT (SELECT avg(cpu_usage) WHERE false) AS smth1");
    assertSet("/uw", "smth1", Doub());
    // This is a type error, even though some interpreters accept this query
    executeQuery("SELECT (SELECT avg(cpu_usage) WHERE false) + \"string\" AS smth");
    assertNotSet("/uw", "smth");
  }

  @Test
  public void testBad10() throws Exception {
    executeQuery("SELECT min(unfold(some_names) + \"xx\") AS smt");
    assertSet("/uw", "smt", Str("agatkaxx"));
    executeQuery("SELECT min(unfold(some_names + \"xx\")) AS beatka");
    assertNotSet("/uw", "beatka");
  }

  @Test
  public void testBad11() throws Exception {
    executeQuery("SELECT (SELECT 2, 3) AS smth");
    assertNotSet("/", "smth");
  }

  @Test
  public void testBad12() throws Exception {
    executeQuery("SELECT 2 AS two, 2 AS two");
    executeQuery("SELECT (SELECT 3 AS three) AS four");
    assertNotSet("/", "two");
    assertNotSet("/", "three");
    assertNotSet("/", "four");
  }

  @Test
  public void testBad13() throws Exception {
    executeQuery("SELECT 2 AS &special, true AS control");
    assertNotSet("/", "control");
  }

  @Test
  public void testBad14() throws Exception {
    executeQuery("SELECT epoch() + epoch() AS plus");
    executeQuery("SELECT epoch() - epoch() AS minus");
    assertNotSet("/", "plus");
    assertSet("/", "minus", Dur(0));
  }

  @Test
  public void testBad15() throws Exception {
    executeQuery("SELECT first(\"baba\", name) AS names");
    executeQuery("SELECT first(1) AS missing");
    executeQuery("SELECT first(1, name, 3) AS extra");
    executeQuery("SELECT first(1, name) AS ok ORDER BY name ASC NULLS LAST");
    assertNotSet("/", "names");
    assertNotSet("/", "missing");
    assertNotSet("/", "extra");
    assertSet("/", "ok", List(TStr(), Str("pjwstk")));
  }

  @Test
  public void testBad16() throws Exception {
    executeQuery("");
    assertNotSet("/", "");
  }

  @Test
  public void testBad17() throws Exception {
    executeQuery("");
    assertNotSet("/", "");
  }

  @Test
  public void testBad18() throws Exception {
    executeQuery("");
    assertNotSet("/", "");
  }

  @Test
  public void testBad19() throws Exception {
    executeQuery("");
    assertNotSet("/", "");
  }

  @Test
  public void testBad20() throws Exception {
    executeQuery("");
    assertNotSet("/", "");
  }

  @Test
  public void testExample0() throws Exception {
    executeQuery("SELECT 2 + 2 AS two_plus_two");
    assertSet("/", "two_plus_two", Int(4));
  }

  @Test
  public void testExample1() throws Exception {
    executeQuery("SELECT count(members) AS members_count");
    assertSet("/uw", "members_count", Int(3));
    assertNotSet("/", "members_count");
  }

  @Test
  public void testExample2() throws Exception {
    executeQuery(
        "SELECT first(2, unfold(contacts)) AS new_contacts ORDER BY num_cores ASC NULLS FIRST, "
        + "cpu_usage DESC NULLS LAST");
    assertSet("/uw", "new_contacts", List(TCont(), Cont("UW1A"), Cont("UW1B")));
    assertNotSet("/", "new_contacts");
  }

  @Test
  public void testExample2v1() throws Exception {
    executeQuery("SELECT first(2, unfold(contacts)) AS new_contacts ORDER BY cpu_usage "
                 + "DESC NULLS LAST, num_cores ASC NULLS FIRST");
    assertSet("/uw", "new_contacts", List(TCont(), Cont("UW3A"), Cont("UW3B")));
  }

  @Test
  public void testExample3() throws Exception {
    executeQuery("SELECT sum(num_cores * 2) AS ncores WHERE cpu_usage < ( SELECT avg(cpu_usage))");
    assertSet("/pjwstk", "ncores", Int(14));
    assertNotSet("/", "ncores");
  }

  @Test
  public void testExample4() throws Exception {
    executeQuery("SELECT count(num_cores - size(some_names)) AS sth");
    assertSet("/uw", "sth", Int(2));
    assertNotSet("/pjwstk", "sth");
  }

  @Test
  public void testExample5() throws Exception {
    executeQuery(
        "SELECT min(sum(distinct(2*level)) + 38 * size(contacts)) AS sth WHERE num_cores < 8");
    assertSet("/uw", "sth", Int(42));
    assertNotSet("/", "sth");
  }

  @Test
  public void testExample6() throws Exception {
    executeQuery("SELECT random(10, cpu_usage * num_cores / 10) AS sth");
    assertSet("/pjwstk", "sth", List(TDoub(), Doub(0.07), Doub(0.52)));
    assertNotSet("/", "sth");
  }

  @Test
  public void testExample7v2() throws Exception {
    executeQuery("SELECT first(1, name) AS concat_name WHERE num_cores >= "
                 + "(SELECT min(num_cores) ORDER BY timestamp) ORDER BY creation ASC NULLS "
                 + "LAST");
    assertSet("/pjwstk", "concat_name", List(TStr(), Str("whatever01")));
  }

  @Test
  public void testExample8() throws Exception {
    executeQuery("SELECT sum(cardinality) AS cardinality");
    assertSet("/", "cardinality", Int(5));
    assertSet("/uw", "cardinality", Int(3));
    assertSet("/pjwstk", "cardinality", Int(2));
  }

  @Test
  public void testExample9() throws Exception {
    executeQuery("SELECT to_set(random(100, unfold(contacts))) AS contacts");
    assertSet("/", "contacts",
        Set(TCont(), Cont("PJ1"), Cont("PJ2"), Cont("UW1A"), Cont("UW1B"), Cont("UW1C"),
            Cont("UW2A"), Cont("UW3A"), Cont("UW3B")));
  }

  @Test
  public void testExample10() throws Exception {
    executeQuery("SELECT land(cpu_usage < 0.5) AS cpu_ok");
    assertSet("/pjwstk", "cpu_ok", Bool(true));
    assertSet("/uw", "cpu_ok", Bool(false));
  }

  @Test
  public void testExample11() throws Exception {
    executeQuery(
        "SELECT min(name) AS min_name, to_string(first(1, name)) AS max_name ORDER BY name DESC");
    assertSet("/", "min_name", Str("pjwstk"));
    assertSet("/", "max_name", Str("[ uw ]"));
  }

  @Test
  public void testExample12() throws Exception {
    executeQuery("SELECT epoch() AS epoch, land(timestamp > epoch()) AS afterY2K");
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
    executeQuery("SELECT min(timestamp) + (max(timestamp) - epoch())/2 AS t2");
    // The problem here is that (at least for Java) CET is not defined for dates within DST.
    // I have decided to follow the convention and dates from DST must be specified with CEST
    // time zone. Internal representation is global in the sense that it uses an offset from Unix
    // "epoch time", so this assumption affects string representation only.
    assertSet("/uw", "t2", Str("2019/04/16 05:31:30.000 CEST").to().Time());
  }

  @Test
  public void testExample14() throws Exception {
    executeQuery("SELECT avg(cpu_usage) AS anull WHERE isnull(cpu_usage)");
    assertSet("/uw", "anull", Doub());
  }

  @Test
  public void testExample15() throws Exception {
    executeQuery("SELECT (SELECT avg(cpu_usage) WHERE false) AS smth");
    assertSet("/uw", "smth", Doub());
  }

  @Test
  public void testExample16() throws Exception {
    installQuery(AttributeName.valueOfReserved("&ex16_1"),
        new CAQuery("SELECT first(1, some_names) AS some_names ORDER BY name ASC"));
    installQuery(AttributeName.valueOfReserved("&ex16_2"),
        new CAQuery("SELECT count(distinct(unfold(some_names))) AS names_no ORDER BY name"));
    recomputeQueries();
    assertSet("/uw", "some_names", List(TList(TStr()), List(TStr())));
    assertSet("/", "some_names", List(TList(TList(TStr())), List(TList(TStr()), List(TStr()))));
    assertSet("/uw", "names_no", Int(5));
    assertSet("/", "names_no", Int(1));
  }

  @Test
  public void testExample17() throws Exception {
    removeQuery(executeQuery("SELECT sum(cardinality) AS base"));
    executeQuery("SELECT sum(cardinality) + sum(base) AS base");
    assertSet("/", "base", Int(5));
    assertSet("/uw", "base", Int(3));
    assertSet("/pjwstk", "base", Int(2));
  }

  @Test
  public void testExample18() throws Exception {
    executeQuery("SELECT min(cpu_usage) AS min_usage, max(cpu_usage) AS max_usage");
    assertSet("/uw", "min_usage", Doub(0.1));
    assertSet("/uw", "max_usage", Doub(0.9));
  }

  @Test
  public void testExample19() throws Exception {
    executeQuery("SELECT to_set(first(100, name)) AS ups_names WHERE has_ups");
    assertSet("/uw", "ups_names", Set(TStr(), Str("khaki13")));
  }

  @Test
  public void testExample20() throws Exception {
    executeQuery("SELECT first(100, is_null(has_ups)) AS ups_nulls ORDER BY name ASC");
    assertSet("/uw", "ups_nulls", List(TBool(), Bool(false), Bool(false), Bool(true)));
  }

  @Test
  public void testExample21() throws Exception {
    executeQuery("SELECT (2 < 3) AND (3 > 2) AND NOT (2 > 3) AND NOT (3 < 2) AND (2 <> 3) AND "
                 + "(2 = 2) AND (2 <= 2) AND (2 <= 3) AND (3 >= 2) AND (2 >= 2) AS test");
    assertSet("/", "test", Bool(true));
  }

  @Test
  public void testExample22() throws Exception {
    executeQuery("SELECT to_set(first(1, members)) AS members ORDER BY name");
    assertSet("/", "members",
        Set(TSet(TSet(TCont())), Set(TSet(TCont()), Set(TCont(), Cont("PJ1")))));
  }

  /*
  @Test
  public void testExample() throws Exception {
    executeQuery("");
    assertSet("/", "", );
  }
  */

  /*
  @Test
  public void testBad() throws Exception {
    executeQuery("");
    assertNotSet("/", "");
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
