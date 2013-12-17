package stupaq.guava.util.concurrent;

import com.google.common.base.Preconditions;
import com.google.common.util.concurrent.ListeningExecutorService;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.Callable;

public class AsynchronousInvoker implements InvocationHandler {
  private final Object listener;
  private final ListeningExecutorService executor;

  public AsynchronousInvoker(Object listener, ListeningExecutorService executor) {
    this.listener = listener;
    this.executor = executor;
  }

  @Override
  public Object invoke(Object proxy, final Method method, final Object[] args) throws Exception {
    Callable<Object> callable = new Callable<Object>() {
      @Override
      public Object call() throws Exception {
        try {
          return method.invoke(listener, args);
        } catch (InvocationTargetException e) {
          throw e.getTargetException() instanceof Exception ? (Exception) e.getTargetException() :
              e;
        }
      }
    };
    if (method.isAnnotationPresent(DirectInvocation.class)) {
      callable.call();
    } else if (method.isAnnotationPresent(DeferredInvocation.class)) {
      // Nothing else makes sense as we cannot guarantee any value at the time when we submit a task
      Preconditions.checkState(method.getReturnType() == void.class);
      executor.submit(callable);
    } else {
      throw new IllegalStateException(
          AsynchronousInvoker.class.getSimpleName() + " cannot decide invocation mode.");
    }
    return null;
  }

  @Retention(value = RetentionPolicy.RUNTIME)
  @Target(value = ElementType.METHOD)
  public static @interface DirectInvocation {
  }

  @Retention(value = RetentionPolicy.RUNTIME)
  @Target(value = ElementType.METHOD)
  public static @interface DeferredInvocation {
  }
}
