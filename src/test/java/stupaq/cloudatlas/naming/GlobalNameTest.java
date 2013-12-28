package stupaq.cloudatlas.naming;

import org.junit.Test;

import stupaq.cloudatlas.naming.GlobalName.Builder;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static stupaq.cloudatlas.naming.LocalName.getNotRoot;
import static stupaq.cloudatlas.naming.LocalName.getRoot;

public class GlobalNameTest {
  @Test
  public void testBuilderOK() throws Exception {
    assertEquals("/asd/fgh",
        new Builder().parent(getRoot()).child(getNotRoot("asd")).child(getNotRoot("fgh")).build()
            .toString());
    assertEquals("/asd/fgh",
        new Builder().child(getNotRoot("asd")).parent(getRoot()).child(getNotRoot("fgh")).build()
            .toString());
    assertEquals("/fgh/asd",
        new Builder().child(getNotRoot("fgh")).child(getNotRoot("asd")).parent(getRoot()).build()
            .toString());
  }

  @Test(expected = Exception.class)
  public void testBuilderBad0() throws Exception {
    new Builder().child(getNotRoot("asd")).child(getNotRoot("fgh")).build();
    fail();
  }

  @Test(expected = Exception.class)
  public void testBuilderBad1() throws Exception {
    new Builder().child(getRoot()).parent(getNotRoot("asd")).child(getNotRoot("fgh")).build();
    fail();
  }

  @Test(expected = Exception.class)
  public void testBuilderBad2() throws Exception {
    new Builder().parent(getRoot()).child(getNotRoot("asd")).parent(getRoot()).build();
    fail();
  }

  @Test(expected = Exception.class)
  public void testBuilderBad3() throws Exception {
    new Builder().child(getNotRoot("qwe")).child(getNotRoot("asd")).child(getNotRoot("fgh"))
        .build();
    fail();
  }

  @Test
  public void testParseOK() throws Exception {
    assertEquals(
        new Builder().parent(getRoot()).child(getNotRoot("asd")).child(getNotRoot("fgh")).build(),
        GlobalName.parse("/asd/fgh"));
  }

  @Test(expected = Exception.class)
  public void testParseBad0() throws Exception {
    GlobalName.parse("/uw/");
    fail();
  }

  @Test(expected = Exception.class)
  public void testParseBad1() throws Exception {
    GlobalName.parse("//");
    fail();
  }

  @Test(expected = Exception.class)
  public void testParseBad2() throws Exception {
    GlobalName.parse("uw/fgh");
    fail();
  }

  @Test(expected = Exception.class)
  public void testParseBad3() throws Exception {
    GlobalName.parse("");
    fail();
  }

  @Test
  public void testIterator() throws Exception {
    int expected = 0;
    for (LocalName local : GlobalName.parse("/1/2/3/4")) {
      assertEquals(expected == 0 ? "/" : "" + expected, local.toString());
      expected++;
    }
  }

  @Test
  public void testLeafLevel() throws Exception {
    assertEquals(0, new Builder().parent(getRoot()).build().leafLevel());
    assertEquals(1, new Builder().child(getNotRoot("asd")).parent(getRoot()).build().leafLevel());
    assertEquals(2,
        new Builder().child(getNotRoot("asd")).child(getNotRoot("asd")).parent(getRoot()).build()
            .leafLevel());
  }
}
