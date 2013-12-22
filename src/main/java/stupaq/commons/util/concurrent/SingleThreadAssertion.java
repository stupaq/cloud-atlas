package stupaq.commons.util.concurrent;

import com.google.common.base.Preconditions;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class SingleThreadAssertion {
  private static final Log LOG = LogFactory.getLog(SingleThreadAssertion.class);
  private final boolean enabled;
  private Thread firstSeen = null;

  @SuppressWarnings("ConstantConditions")
  public SingleThreadAssertion() {
    boolean enabled = false;
    assert enabled = true;
    this.enabled = enabled;
    LOG.info("Assertions are " + (enabled ? "enabled" : "disabled"));
  }

  public void check() {
    if (enabled) {
      synchronized (this) {
        if (firstSeen == null) {
          firstSeen = Thread.currentThread();
        }
        Preconditions.checkState(firstSeen == Thread.currentThread());
      }
    }
  }
}
