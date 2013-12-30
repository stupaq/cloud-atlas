package stupaq.cloudatlas.gossiping.dataformat;

import com.google.common.collect.Sets;

import org.junit.Test;

import java.util.Iterator;
import java.util.Set;

import stupaq.cloudatlas.services.busybody.sessions.SessionId;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class GossipIdTest {
  private final SessionId session = new SessionId();

  @Test
  public void testNextGossip() throws Exception {
    assertEquals(new GossipId(session), new GossipId(session));
    GossipId gossip = new GossipId(session);
    Set<GossipId> seen = Sets.newHashSet();
    int count = Byte.MAX_VALUE - Byte.MIN_VALUE + 1;
    for (int i = 0; i < count; i++) {
      assertTrue(seen.add(gossip));
      gossip = gossip.nextGossip();
    }
    assertFalse(seen.add(gossip));
    assertEquals(new GossipId(session), gossip);
  }

  @Test
  public void testFirstFrame() throws Exception {
    GossipId value0 = new GossipId(new SessionId()).nextGossip();
    GossipId value1 = new GossipId(new SessionId().nextSession()).nextGossip();
    assertFalse(value0.equals(value1));
    assertEquals(new FrameId(value0, 123), value0.framesIterator(123).next());
    assertEquals(new FrameId(value1, 123), value1.framesIterator(123).next());
  }

  @Test
  public void testFramesIterator() throws Exception {
    GossipId value = new GossipId(session);
    Iterator<FrameId> frames = value.framesIterator(3);
    assertTrue(frames.hasNext());
    assertEquals(new FrameId(value, 3, 0), frames.next());
    assertTrue(frames.hasNext());
    assertEquals(new FrameId(value, 3, 1), frames.next());
    assertTrue(frames.hasNext());
    assertEquals(new FrameId(value, 3, 2), frames.next());
    assertFalse(frames.hasNext());
  }

  @Test
  public void testFramesIterable() throws Exception {
    GossipId value = new GossipId(session);
    Iterator<FrameId> frames = value.framesIterator(3);
    for (FrameId frameId : value.frames(3)) {
      assertEquals(frames.next(), frameId);
    }
    assertFalse(frames.hasNext());
  }
}
