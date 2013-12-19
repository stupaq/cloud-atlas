package stupaq.commons.util.concurrent;

import com.google.common.base.Preconditions;
import com.google.common.util.concurrent.ListeningExecutorService;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.Callable;

import javax.annotation.Nonnull;

public class AsynchronousInvoker implements InvocationHandler {
  private static final Log LOG = LogFactory.getLog(AsynchronousInvoker.class);
  private final Object listener;
  private final ListeningExecutorService executor;

  public AsynchronousInvoker(@Nonnull Object delegate, @Nonnull ListeningExecutorService executor) {
    Preconditions.checkNotNull(delegate);
    Preconditions.checkNotNull(executor);
    this.listener = delegate;
    this.executor = executor;
  }

  @Override
  public Object invoke(Object proxy, final Method method, final Object[] args) throws Exception {
    // Response (if any) will be sent asynchronously
    Preconditions.checkState(method.getReturnType() == void.class);
    Callable<Object> callable = new Callable<Object>() {
      @Override
      public Object call() throws Exception {
        try {
          method.setAccessible(true);
          return method.invoke(listener, args);
        } catch (InvocationTargetException e) {
          LOG.error("Invoker serving " + listener.getClass() + " call failed",
              e.getTargetException());
          throw e.getCause() instanceof Exception ? (Exception) e.getCause() : e;
        } finally {
          method.setAccessible(false);
        }
      }
    };
    if (method.isAnnotationPresent(DirectInvocation.class)) {
      return callable.call();
    } else if (method.isAnnotationPresent(ScheduledInvocation.class)) {
      // Nothing else makes sense as we cannot guarantee any value at the time when we submit a task
      executor.submit(callable);
      return null;
    } else {
      throw new IllegalStateException(
          AsynchronousInvoker.class.getSimpleName() + " cannot decide invocation mode.");
    }
  }

  @Retention(value = RetentionPolicy.RUNTIME)
  @Target(value = ElementType.METHOD)
  public static @interface DirectInvocation {
  }

  @Retention(value = RetentionPolicy.RUNTIME)
  @Target(value = ElementType.METHOD)
  public static @interface ScheduledInvocation {
  }
}
