package stupaq.cloudatlas.parser;

import org.junit.Test;

import stupaq.cloudatlas.parser.QueryLanguage.Absyn.Program;

import static org.junit.Assert.fail;

public class QueryParserTest {

  @Test
  public void testOfficial() throws Exception {
    assertSuccess("SELECT count(*) AS child_zone_count");
    assertSuccess("SELECT avg(cpu_load * num_cpus) AS cpu_load, sum(num_cpus) AS num_cpus");
    assertSuccess("SELECT DISTINCT random(5, unfold(contacts)) AS contacts");
    assertSuccess("SELECT sum(total_disk) AS total_disk, sum(used_disk) AS used_disk");
    assertSuccess(
        "SELECT ALL first(3, name) AS heavy_disk_users WHERE total_disk > 0 ORDER BY used_disk / total_disk");
  }

  private Program assertSuccess(String str) throws Exception {
    try (QueryParser parser = new QueryParser(str)) {
      return parser.parseProgram();
    }
  }

  private Exception assertFailure(String str) {
    try (QueryParser parser = new QueryParser(str)) {
    } catch (Exception e) {
      return e;
    }
    fail();
    return null;
  }
}
