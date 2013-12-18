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

import stupaq.cloudatlas.messaging.MessageListener.AbstractMessageListener;
import stupaq.commons.util.concurrent.AsynchronousInvoker.ScheduledInvocation;
import stupaq.commons.util.concurrent.AsynchronousInvoker.DirectInvocation;

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

  private interface TestListener1Contract {
    @Subscribe
    @DirectInvocation
    public void handle1(TestMessage1 message);

    @Subscribe
    @ScheduledInvocation
    public void handle2(TestMessage2 message);
  }

  private static class TestMessage1 extends Message {
  }

  private static class TestMessage2 extends Message {
  }

  private class TestListener1 extends AbstractMessageListener implements TestListener1Contract {
    protected TestListener1() {
      super(singleThreaded, TestListener1Contract.class);
    }

    @Override
    public void handle1(TestMessage1 message) {
      Uninterruptibles.sleepUninterruptibly(TIME_SCALE, TimeUnit.MILLISECONDS);
    }

    @Override
    public void handle2(TestMessage2 message) {
      Uninterruptibles.sleepUninterruptibly(TIME_SCALE, TimeUnit.MILLISECONDS);
    }
  }
}
