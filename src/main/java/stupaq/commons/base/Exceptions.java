package stupaq.commons.base;

import org.apache.commons.logging.Log;

import javax.annotation.Nullable;

public final class Exceptions {
  private Exceptions() {
  }

  public static void cleanup(@Nullable Log log,
      @Nullable Iterable<? extends AutoCloseable> closeables) {
    if (closeables != null) {
      for (AutoCloseable closeable : closeables) {
        try {
          if (closeable != null) {
            closeable.close();
          }
        } catch (Exception e) {
          if (log != null) {
            log.warn("Exception closing: " + closeable, e);
          }
        }
      }
    }
  }
}
