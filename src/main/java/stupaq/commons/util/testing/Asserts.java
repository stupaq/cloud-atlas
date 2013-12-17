package stupaq.commons.util.testing;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class Asserts {
  private Asserts() {
  }

  @SuppressWarnings("unused")
  public static void assertEqualsDebug(Object expected, Object actual) {
    assertEquals(expected, actual);
    System.out.println("expected:\t" + String.valueOf(expected) + '\n' +
        "actual:  \t" + String.valueOf(actual));
  }

  public static void assertLasts(long expected, long start) {
    assertEquals(expected, System.currentTimeMillis() - start, expected / 20);
  }

  public static void assertLastsLess(long expected, long start) {
    assertTrue(expected > System.currentTimeMillis() - start);
  }
}
