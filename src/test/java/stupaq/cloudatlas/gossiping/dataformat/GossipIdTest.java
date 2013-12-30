package stupaq.cloudatlas.gossiping.dataformat;

import org.junit.Test;

import java.util.Iterator;

import stupaq.cloudatlas.services.busybody.sessions.SessionId;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class GossipIdTest {
  private final SessionId sessionId = new SessionId();

  @Test
  public void testFirstFrame() throws Exception {
    GossipId value0 = new GossipId(new SessionId()).nextGossip().nextGossip();
    GossipId value1 = new GossipId(new SessionId().nextSession()).nextGossip().nextGossip();
    assertFalse(value0.equals(value1));
    assertEquals(new FrameId(value0, 123), value0.framesIterator(123).next());
    assertEquals(new FrameId(value1, 123), value1.framesIterator(123).next());
  }

  @Test
  public void testFramesIterator() throws Exception {
    GossipId value = new GossipId(sessionId);
    Iterator<FrameId> iter = value.framesIterator(3);
    assertTrue(iter.hasNext());
    assertEquals(new FrameId(value, 3, 0), iter.next());
    assertTrue(iter.hasNext());
    assertEquals(new FrameId(value, 3, 1), iter.next());
    assertTrue(iter.hasNext());
    assertEquals(new FrameId(value, 3, 2), iter.next());
    assertFalse(iter.hasNext());
  }

  @Test
  public void testFramesIterable() throws Exception {
    GossipId value = new GossipId(sessionId);
    Iterator<FrameId> iter = value.framesIterator(3);
    for (FrameId frameId : value.frames(3)) {
      assertEquals(iter.next(), frameId);
    }
    assertFalse(iter.hasNext());
  }
}
