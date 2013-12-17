package stupaq.cloudatlas.messaging;

import com.google.common.eventbus.Subscribe;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.common.util.concurrent.Uninterruptibles;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import stupaq.commons.util.concurrent.AsynchronousInvoker.DeferredInvocation;
import stupaq.commons.util.concurrent.AsynchronousInvoker.DirectInvocation;
import stupaq.compact.TypeDescriptor;

import static stupaq.commons.util.testing.Asserts.assertLasts;
import static stupaq.commons.util.testing.Asserts.assertLastsLess;

public class MessageBusTest {
  private static final long TIME_SCALE = 1000L;
  private MessageBus bus;
  private ListeningExecutorService singleThreaded;

  @Before
  public void setUp() throws Exception {
    bus = new MessageBus();
    singleThreaded = MoreExecutors.listeningDecorator(Executors.newSingleThreadExecutor());
  }

  @After
  public void tearDown() throws Exception {
    singleThreaded.shutdownNow();
  }

  @Test
  public void testDirect() throws Exception {
    bus.register(new TestListener1());
    long start = System.currentTimeMillis();
    bus.post(new TestMessage1());
    assertLasts(TIME_SCALE, start);
  }

  @Test
  public void testDeferred() throws Exception {
    bus.register(new TestListener1());
    long start = System.currentTimeMillis();
    bus.post(new TestMessage2());
    assertLastsLess(TIME_SCALE / 10, start);
  }

  public interface TestListener1Contract {
    @Subscribe
    @DirectInvocation
    public void handle1(TestMessage1 message);

    @Subscribe
    @DeferredInvocation
    public void handle2(TestMessage2 message);
  }

  public static abstract class TestMessage extends Message {
    @Override
    public TypeDescriptor descriptor() {
      return null;
    }
  }

  public static class TestMessage1 extends TestMessage {
  }

  public static class TestMessage2 extends TestMessage {
  }

  public class TestListener1 implements TestListener1Contract, MessageListener {
    @Override
    public void handle1(TestMessage1 message) {
      Uninterruptibles.sleepUninterruptibly(TIME_SCALE, TimeUnit.MILLISECONDS);
    }

    @Override
    public void handle2(TestMessage2 message) {
      Uninterruptibles.sleepUninterruptibly(TIME_SCALE, TimeUnit.MILLISECONDS);
    }

    @Override
    public ListeningExecutorService executor() {
      return singleThreaded;
    }

    @Override
    public Class<?> getInterface() {
      return TestListener1Contract.class;
    }
  }
}
