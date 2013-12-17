package stupaq.commons.util.testing;

import static org.junit.Assert.assertEquals;

public class Asserts {
  private Asserts() {
  }

  @SuppressWarnings("unused")
  static void assertEqualsDebug(Object expected, Object actual) {
    assertEquals(expected, actual);
    System.out.println("expected:\t" + String.valueOf(expected) + '\n' +
        "actual:  \t" + String.valueOf(actual));
  }
}
