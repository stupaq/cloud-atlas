package stupaq.cloudatlas.gossiping.dataformat;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class FrameIdTest {
  GossipId gossipId = new GossipId();

  @Test
  public void testNextFrame() throws Exception {
    FrameId frameId = gossipId.firstFrame(2);
    assertEquals(new FrameId(gossipId, 2, 0), frameId);
    assertEquals(new FrameId(gossipId, 2, 1), frameId.nextFrame());
  }

  @Test(expected = IllegalStateException.class)
  public void testNextFrameFailure() throws Exception {
    FrameId frameId = gossipId.firstFrame(2);
    assertEquals(new FrameId(gossipId, 2, 0), frameId);
    assertEquals(new FrameId(gossipId, 2, 1), frameId.nextFrame());
    frameId = frameId.nextFrame().nextFrame();
    fail(frameId.toString());
  }

  @Test
  public void testHasNextFrame() throws Exception {
    FrameId frameId = gossipId.firstFrame(2);
    assertEquals(new FrameId(gossipId, 2, 0), frameId);
    assertTrue(frameId.hasNextFrame());
    assertEquals(new FrameId(gossipId, 2, 1), frameId.nextFrame());
    assertFalse(frameId.nextFrame().hasNextFrame());
  }
}
