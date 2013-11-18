package stupaq.cloudatlas.interpreter.shell;

import com.google.common.base.Preconditions;
import com.google.common.collect.Iterables;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Before;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map.Entry;

import stupaq.cloudatlas.attribute.Attribute;
import stupaq.cloudatlas.attribute.AttributeName;
import stupaq.cloudatlas.attribute.types.CAQuery;
import stupaq.cloudatlas.interpreter.InstalledQueriesUpdater;
import stupaq.cloudatlas.parser.QueryParser;
import stupaq.cloudatlas.zone.ZoneManagementInfo;
import stupaq.cloudatlas.zone.hierarchy.ZoneHierarchy;
import stupaq.cloudatlas.zone.hierarchy.ZoneHierarchy.InPlaceAggregator;
import stupaq.cloudatlas.zone.hierarchy.ZoneHierarchy.InPlaceMapper;
import stupaq.cloudatlas.zone.hierarchy.ZoneHierarchyTestUtils;

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
      // Try to parse
      new QueryParser(query.getQueryString()).parseProgram();
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
        shell.recomputeAttributes();
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

  private void installQuery(final AttributeName name, final CAQuery query) {
    Preconditions.checkArgument(name.isSpecial());
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

  private void recomputeAttributes() {
    root.zipFromLeaves(new InstalledQueriesUpdater());
  }

  @Before
  public void setUp() {
    root = ZoneHierarchyTestUtils.officialExampleHierarchy();
  }
}
