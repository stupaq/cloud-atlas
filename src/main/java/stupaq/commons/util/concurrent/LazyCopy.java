package stupaq.commons.util.concurrent;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.Serializable;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

public abstract class LazyCopy<Type> implements Serializable {
  private static final Log LOG = LogFactory.getLog(LazyCopy.class);
  private boolean deeplyCopied;

  public LazyCopy() {
    this(true);
  }

  public LazyCopy(boolean deeplyCopied) {
    this.deeplyCopied = deeplyCopied;
  }

  protected final void ensureCopied() {
    if (!deeplyCopied) {
      LOG.info("Performing deep copy of object: " + getClass().getSimpleName());
      deepCopy();
      deeplyCopied = true;
    }
  }

  protected abstract void deepCopy();

  public abstract Type export();

  @Documented
  @Retention(RetentionPolicy.RUNTIME)
  @Target({ElementType.CONSTRUCTOR})
  public @interface LazyCopyConstructor {
  }
}
