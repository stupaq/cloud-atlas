package stupaq.cloudatlas.gossiping.dataformat;

import org.junit.Test;

import static com.google.common.primitives.UnsignedInteger.MAX_VALUE;
import static com.google.common.primitives.UnsignedInteger.ONE;
import static com.google.common.primitives.UnsignedInteger.ZERO;
import static org.junit.Assert.assertEquals;

public class GossipIdTest {
  @Test
  public void testStartingValue() throws Exception {
    assertEquals(new GossipId(ZERO), new GossipId());
  }

  @Test
  public void testNextGossip() throws Exception {
    GossipId value = new GossipId(MAX_VALUE.minus(ONE));
    assertEquals(new GossipId(MAX_VALUE), value.nextGossip());
    assertEquals(new GossipId(ZERO), value.nextGossip().nextGossip());
  }

  @Test
  public void testFirstFrame() throws Exception {
    GossipId value = new GossipId(MAX_VALUE.minus(ONE));
    assertEquals(new FrameId(value, 123, (short) 0), value.firstFrame(123));
  }
}
