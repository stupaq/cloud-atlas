package stupaq.cloudatlas.gossiping.dataformat;

import org.junit.Test;

import java.util.Iterator;

import static com.google.common.primitives.UnsignedInteger.MAX_VALUE;
import static com.google.common.primitives.UnsignedInteger.ONE;
import static com.google.common.primitives.UnsignedInteger.ZERO;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

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

  @Test
  public void testFramesIterator() throws Exception {
    GossipId value = new GossipId();
    Iterator<FrameId> iter = value.framesIterator(3);
    assertTrue(iter.hasNext());
    assertEquals(new FrameId(value, 3, (short) 0), iter.next());
    assertTrue(iter.hasNext());
    assertEquals(new FrameId(value, 3, (short) 1), iter.next());
    assertTrue(iter.hasNext());
    assertEquals(new FrameId(value, 3, (short) 2), iter.next());
    assertFalse(iter.hasNext());
  }

  @Test
  public void testFramesIterable() throws Exception {
    GossipId value = new GossipId();
    Iterator<FrameId> iter = value.framesIterator(3);
    for (FrameId frameId : value.frames(3)) {
      assertEquals(iter.next(), frameId);
    }
    assertFalse(iter.hasNext());
  }
}
